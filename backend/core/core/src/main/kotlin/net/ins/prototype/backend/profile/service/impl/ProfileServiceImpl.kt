package net.ins.prototype.backend.profile.service.impl

import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders
import jakarta.transaction.Transactional
import net.ins.prototype.backend.common.exception.EntityNotFoundException
import net.ins.prototype.backend.image.service.ImageService
import net.ins.prototype.backend.profile.converter.ProfileContextToProfileEntityConverter
import net.ins.prototype.backend.profile.dao.model.ProfileEntity
import net.ins.prototype.backend.profile.dao.model.ProfileEsEntity
import net.ins.prototype.backend.profile.dao.repo.ProfileEsRepository
import net.ins.prototype.backend.profile.dao.repo.ProfileRepository
import net.ins.prototype.backend.profile.event.ProfileEventPublisher
import net.ins.prototype.backend.profile.model.Purpose
import net.ins.prototype.backend.profile.service.NewProfileContext
import net.ins.prototype.backend.profile.service.ProfileSearchContext
import net.ins.prototype.backend.profile.service.ProfileSearchService
import net.ins.prototype.backend.profile.service.ProfileService
import net.ins.prototype.backend.profile.validation.NewProfileContextValidator
import org.springframework.data.elasticsearch.client.elc.NativeQuery
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.SearchHits
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ProfileServiceImpl(
    private val profileRepository: ProfileRepository,
    private val profileEsRepository: ProfileEsRepository,
    private val esOperations: ElasticsearchOperations,
    private val newProfileContextValidator: NewProfileContextValidator,
    private val profileContextToEntityConverter: ProfileContextToProfileEntityConverter,
    private val profileEventPublisher: ProfileEventPublisher,
) : ProfileService, ProfileSearchService {

    override fun findAll(search: ProfileSearchContext): List<ProfileEntity> = esEntitySearchHits(search)
        .takeUnless { it.isEmpty }
        ?.map { it.content.dbId }
        ?.let { profileRepository.findAllById(it.toList()) }
        ?: profileRepository.findAll(ProfileRepository.search(search))

    private fun esEntitySearchHits(search: ProfileSearchContext): SearchHits<ProfileEsEntity> {
        val criteria = QueryBuilders.bool().apply {
            must(QueryBuilders.match { m -> m.field("gender").query(search.gender.name) })
            search.countryId?.let { countryId -> must(QueryBuilders.match { m -> m.field("countryId").query(countryId) }) }

            must(
                listOf(
                    QueryBuilders.bool().apply {
                        search.purposes.map { purpose ->
                            when (purpose) {
                                Purpose.DATING -> should { it.match { m -> m.field("purpose.dating").query(true) } }
                                Purpose.SEXTING -> should { it.match { m -> m.field("purpose.sexting").query(true) } }
                                Purpose.RELATIONSHIPS -> should { it.match { m -> m.field("purpose.relationships").query(true) } }
                            }
                        }
                    }.build()._toQuery()
                )
            )
        }

        val query = NativeQuery.builder()
            .withQuery(criteria.build()._toQuery())
            .build()

        return esOperations.search(query, ProfileEsEntity::class.java)
    }

    @Transactional
    override fun create(newProfile: NewProfileContext): Long {
        newProfileContextValidator.validate(newProfile)
        val dbProfile = profileRepository.save(profileContextToEntityConverter.convert(newProfile))
        profileEventPublisher.publish(dbProfile)
        return requireNotNull(dbProfile.id)
    }

    @Transactional
    override fun index(profileEsEntity: ProfileEsEntity) {
        val dbProfile = getById(profileEsEntity.dbId)
        profileEsRepository.save(profileEsEntity)
        profileRepository.save(dbProfile.apply { lastIndexedAt = LocalDateTime.now() })
    }

    override fun getById(id: Long): ProfileEntity = profileRepository.findByIdOrNull(id) ?: throw EntityNotFoundException("No profile found by id: ${id}")
}
