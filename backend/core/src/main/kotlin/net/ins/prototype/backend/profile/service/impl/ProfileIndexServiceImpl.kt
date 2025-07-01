package net.ins.prototype.backend.profile.service.impl

import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders
import jakarta.transaction.Transactional
import net.ins.prototype.backend.common.exception.EntityNotFoundException
import net.ins.prototype.backend.profile.dao.model.ProfileEntity
import net.ins.prototype.backend.profile.dao.model.ProfileEsEntity
import net.ins.prototype.backend.profile.dao.model.PurposeEsSubEntity
import net.ins.prototype.backend.profile.dao.repo.ProfileEsRepository
import net.ins.prototype.backend.profile.event.ProfileCreatedEvent
import net.ins.prototype.backend.profile.event.ProfileUpdatedEvent
import net.ins.prototype.backend.profile.model.Purpose
import net.ins.prototype.backend.profile.service.ProfileIndexService
import net.ins.prototype.backend.profile.service.ProfileService
import net.ins.prototype.backend.profile.service.context.ProfileSearchContext
import net.ins.prototype.backend.profile.validation.ProfileSearchContextValidator
import org.springframework.data.elasticsearch.NoSuchIndexException
import org.springframework.data.elasticsearch.client.elc.NativeQuery
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.SearchHits
import org.springframework.stereotype.Service

@Service
class ProfileIndexServiceImpl(
    private val profileService: ProfileService,
    private val esOperations: ElasticsearchOperations,
    private val profileEsRepository: ProfileEsRepository,
    private val profileSearchContextValidator: ProfileSearchContextValidator,
) : ProfileIndexService {

    override fun findAll(search: ProfileSearchContext): List<ProfileEntity> =
        esEntitySearchHits(profileSearchContextValidator.validate(search))
            ?.takeUnless { it.isEmpty }
            ?.map { it.content.dbId }
            ?.let { profileService.getByIds(it.toList()) }
            ?: profileService.findAll(search)

    @Transactional
    override fun index(profileCreatedEvent: ProfileCreatedEvent) {
        profileService.getById(profileCreatedEvent.dbId) // existence validation
        val esProfile = profileEsRepository.save(fromProfileCreatedEvent(profileCreatedEvent))
        profileService.markIndexed(profileCreatedEvent.dbId, requireNotNull(esProfile.id))
    }

    @Transactional
    override fun updateIndex(profileUpdatedEvent: ProfileUpdatedEvent) {
        val esProfile = profileEsRepository.findByDbId(profileUpdatedEvent.dbId)
            ?: throw EntityNotFoundException("No indexed profile entity found in ES index with dbId ${profileUpdatedEvent.dbId}")
        val dbProfiles = profileService.getById(profileUpdatedEvent.dbId)
        val profileEsEntity = ProfileEsEntity(
            id = requireNotNull(esProfile.id),
            dbId = profileUpdatedEvent.dbId,
            gender = dbProfiles.gender,
            birth = profileUpdatedEvent.birth,
            countryId = profileUpdatedEvent.countryId,
            purpose = toPurposeEsEntity(profileUpdatedEvent.purposes)
        )
        profileEsRepository.save(profileEsEntity)
        profileService.markIndexed(profileUpdatedEvent.dbId, requireNotNull(esProfile.id))
    }

    private fun esEntitySearchHits(search: ProfileSearchContext): SearchHits<ProfileEsEntity>? {
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

        return runCatching { esOperations.search(query, ProfileEsEntity::class.java) }.getOrElse {
            when {
                it is NoSuchIndexException -> null
                else -> throw it
            }
        }
    }

    private fun fromProfileCreatedEvent(source: ProfileCreatedEvent): ProfileEsEntity {
        return ProfileEsEntity(
            dbId = requireNotNull(source.dbId),
            gender = source.gender,
            birth = source.birth,
            countryId = source.countryId,
            purpose = toPurposeEsEntity(source.purposes),
        )
    }

    private fun toPurposeEsEntity(purposes: Set<Purpose>): PurposeEsSubEntity = PurposeEsSubEntity(
        dating = Purpose.DATING in purposes,
        sexting = Purpose.SEXTING in purposes,
        relationships = Purpose.RELATIONSHIPS in purposes,
    )
}
