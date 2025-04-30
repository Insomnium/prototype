package net.ins.prototype.backend.profile.service.impl

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery
import jakarta.transaction.Transactional
import net.ins.prototype.backend.profile.dao.model.ProfileEntity
import net.ins.prototype.backend.profile.dao.model.ProfileEsEntity
import net.ins.prototype.backend.profile.dao.model.PurposeEsSubEntity
import net.ins.prototype.backend.profile.dao.repo.ProfileEsRepository
import net.ins.prototype.backend.profile.dao.repo.ProfileRepository
import net.ins.prototype.backend.profile.model.Purpose
import net.ins.prototype.backend.profile.model.calculateMask
import net.ins.prototype.backend.profile.service.NewProfileContext
import net.ins.prototype.backend.profile.service.ProfileSearchContext
import net.ins.prototype.backend.profile.service.ProfileService
import net.ins.prototype.backend.profile.service.validator.impl.NewProfileContextValidator
import org.springframework.data.elasticsearch.client.elc.NativeQuery
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.SearchHits
import org.springframework.data.elasticsearch.core.query.Criteria
import org.springframework.data.elasticsearch.core.query.CriteriaQuery
import org.springframework.stereotype.Service
import java.util.Arrays

@Service
class ProfileServiceImpl(
    private val profileRepository: ProfileRepository,
    private val profileEsRepository: ProfileEsRepository,
    private val esOperations: ElasticsearchOperations,
    private val newProfileContextValidator: NewProfileContextValidator,
) : ProfileService {

    override fun findAll(search: ProfileSearchContext): List<ProfileEntity> = esEntitySearchHits(search)
        .takeUnless { it.isEmpty }
        ?.map { it.content.dbId }
        ?.let { profileRepository.findAllById(it) }
//        ?: profileRepository.findAll(ProfileRepository.search(search)) // FIXME: does it make sense? ES should be trustworthy
        ?: emptyList()

    private fun esEntitySearchHits(search: ProfileSearchContext): SearchHits<ProfileEsEntity> {
        val genderCriteria = Criteria("gender").`is`(search.gender.code)
        val locationCriteria = search.countryId?.let { Criteria("countryId").`is`(it) }
        val purposeCriteria = search.purposes.takeUnless { it.isEmpty() }?.map {
            when (it) {
                Purpose.DATING -> Criteria("purpose.dating").`is`(true)
                Purpose.SEXTING -> Criteria("purpose.sexting").`is`(true)
                Purpose.RELATIONSHIPS -> Criteria("purpose.relationships").`is`(true)
            }
//        }.fold(Criteria(), Criteria::or)
        }?.reduce { acc, criteria -> acc.or(criteria) }

//        val searchCriteria: Criteria = purposeCriteria
        val searchCriteria: Criteria = Criteria().apply {
            and(genderCriteria)
            locationCriteria?.let { and(it) }
            purposeCriteria?.let { and(it) }
        }

        val criteria = QueryBuilders.bool().apply {
            must(QueryBuilders.term { term -> term.field("gender").value(search.gender.code.toString()) })
            search.countryId?.let { countryId -> must(QueryBuilders.term { term -> term.field("countryId").value(countryId) }) }
            search.purposes.map { purpose ->
                when (purpose) {
                    Purpose.DATING -> should(QueryBuilders.term { term -> term.field("purpose.dating").value(true) })
                    Purpose.SEXTING -> should(QueryBuilders.term { term -> term.field("purpose.sexting").value(true) })
                    Purpose.RELATIONSHIPS -> should(QueryBuilders.term { term -> term.field("purpose.relationships").value(true) })
                }
            }
        }

        val query = NativeQuery.builder()
            .withQuery(criteria.build()._toQuery())
            .build()

        return esOperations.search(query, ProfileEsEntity::class.java)
//        return esOperations.search(CriteriaQuery(searchCriteria), ProfileEsEntity::class.java)
    }

    @Transactional
    override fun create(newProfile: NewProfileContext): Long {
        newProfileContextValidator.validate(newProfile)
        return requireNotNull(
            profileRepository.save(
                ProfileEntity(
                    title = newProfile.title,
                    birth = newProfile.birth,
                    gender = newProfile.gender,
                    countryId = newProfile.countryId,
                    purposeMask = newProfile.purposes.calculateMask()
                ) // FIXME: replace with converter?
            ).also {
                profileEsRepository.save(
                    ProfileEsEntity(
                        dbId = requireNotNull(it.id),
                        gender = newProfile.gender.code.toString(),
                        birth = newProfile.birth,
                        countryId = newProfile.countryId,
                        purpose = PurposeEsSubEntity(
                            dating = Purpose.DATING in newProfile.purposes,
                            sexting = Purpose.SEXTING in newProfile.purposes,
                            relationships = Purpose.RELATIONSHIPS in newProfile.purposes,
                        ),
                    ) // TODO: replace with converter
                )
            }.id
        )
    }
}

// 9: V, dating,relationships
// 10: X, dating,sexting
// 11: Z, sexting
// 12: I, relationships
