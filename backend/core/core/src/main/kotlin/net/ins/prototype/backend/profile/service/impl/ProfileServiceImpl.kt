package net.ins.prototype.backend.profile.service.impl

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
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.SearchHits
import org.springframework.data.elasticsearch.core.query.Criteria
import org.springframework.data.elasticsearch.core.query.CriteriaQuery
import org.springframework.stereotype.Service

@Service
class ProfileServiceImpl(
    private val profileRepository: ProfileRepository,
    private val profileEsRepository: ProfileEsRepository,
    private val esOperations: ElasticsearchOperations,
) : ProfileService {

    override fun findAll(search: ProfileSearchContext): List<ProfileEntity> = esEntitySearchHits(search)
        .takeUnless { it.isEmpty }
        ?.map { it.content.dbId }
        ?.let { profileRepository.findAllById(it) }
        ?: profileRepository.findAll(ProfileRepository.search(search))

    private fun esEntitySearchHits(search: ProfileSearchContext): SearchHits<ProfileEsEntity> {
        val criteria = search.purposes.map {
            when (it) {
                Purpose.DATING -> Criteria("purpose.dating").`is`(true)
                Purpose.SEXTING -> Criteria("purpose.sexting").`is`(true)
                Purpose.RELATIONSHIPS -> Criteria("purpose.relationships").`is`(true)
            }
        }
        val searchCriteria = criteria.fold(Criteria()) { l, r ->
            l.or(r)
        }.and(Criteria("gender").`is`(search.gender.code))

        val esResult = esOperations.search(CriteriaQuery(searchCriteria), ProfileEsEntity::class.java)
        return esResult
    }

    @Transactional
    override fun create(newProfile: NewProfileContext): Long = requireNotNull(
        profileRepository.save(
            ProfileEntity(
                title = newProfile.title,
                birth = newProfile.birth,
                gender = newProfile.gender,
                purposeMask = newProfile.purposes.calculateMask()
            ) // FIXME: replace with converter?
        ).also {
            profileEsRepository.save(
                ProfileEsEntity(
                    dbId = requireNotNull(it.id),
                    gender = newProfile.gender.code.toString(),
                    birth = newProfile.birth,
                    purpose = PurposeEsSubEntity(
                        dating = Purpose.DATING in newProfile.purposes,
                        sexting = Purpose.SEXTING in newProfile.purposes,
                        relationships = Purpose.RELATIONSHIPS in newProfile.purposes,
                    )
                ) // TODO: replace with converter
            )
        }.id
    )
}
