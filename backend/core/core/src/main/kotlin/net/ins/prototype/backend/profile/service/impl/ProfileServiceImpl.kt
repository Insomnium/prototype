package net.ins.prototype.backend.profile.service.impl

import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders
import jakarta.transaction.Transactional
import net.ins.prototype.backend.profile.converter.ProfileContextToProfileEntityConverter
import net.ins.prototype.backend.profile.converter.ProfileEntityToProfileEsEntityConverter
import net.ins.prototype.backend.profile.dao.model.ProfileEntity
import net.ins.prototype.backend.profile.dao.model.ProfileEsEntity
import net.ins.prototype.backend.profile.dao.repo.ProfileEsRepository
import net.ins.prototype.backend.profile.dao.repo.ProfileRepository
import net.ins.prototype.backend.profile.model.Purpose
import net.ins.prototype.backend.profile.service.NewProfileContext
import net.ins.prototype.backend.profile.service.ProfileSearchContext
import net.ins.prototype.backend.profile.service.ProfileService
import net.ins.prototype.backend.profile.service.validator.impl.NewProfileContextValidator
import org.springframework.data.elasticsearch.client.elc.NativeQuery
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.SearchHits
import org.springframework.stereotype.Service

@Service
class ProfileServiceImpl(
    private val profileRepository: ProfileRepository,
    private val profileEsRepository: ProfileEsRepository,
    private val esOperations: ElasticsearchOperations,
    private val newProfileContextValidator: NewProfileContextValidator,
    private val profileContextToEntityConverter: ProfileContextToProfileEntityConverter,
    private val profileEntityToProfileEsEntityConverter: ProfileEntityToProfileEsEntityConverter,
) : ProfileService {

    override fun findAll(search: ProfileSearchContext): List<ProfileEntity> = esEntitySearchHits(search)
        .takeUnless { it.isEmpty }
        ?.map { it.content.dbId }
        ?.let { profileRepository.findAllById(it) }
        ?: profileRepository.findAll(ProfileRepository.search(search))

    private fun esEntitySearchHits(search: ProfileSearchContext): SearchHits<ProfileEsEntity> {

        val criteria = QueryBuilders.bool().apply {
            must(QueryBuilders.match { m -> m.field("gender").query(search.gender.code.toString()) })
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
        return requireNotNull(
            profileRepository.save(profileContextToEntityConverter.convert(newProfile)).also {
                profileEsRepository.save(profileEntityToProfileEsEntityConverter.convert(it)) // TODO: propagate via Kafka
            }.id
        )
    }
}