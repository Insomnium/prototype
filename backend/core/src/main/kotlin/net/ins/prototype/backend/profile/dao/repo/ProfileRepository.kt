package net.ins.prototype.backend.profile.dao.repo

import net.ins.prototype.backend.profile.dao.model.ProfileEntity
import net.ins.prototype.backend.profile.model.Gender
import net.ins.prototype.backend.profile.model.Purpose
import net.ins.prototype.backend.profile.service.context.ProfileSearchContext
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface ProfileRepository : JpaRepository<ProfileEntity, Long>, JpaSpecificationExecutor<ProfileEntity> {

    @Query("update ProfileEntity p set p.indexId = :indexId, p.lastIndexedAt = :indexedAt where p.id = :id")
    @Modifying
    fun setIndexedAt(@Param("id") id: Long, @Param("indexId") indexId: String, @Param("indexedAt") indexedAt: LocalDateTime)

    companion object {

        private fun genderIs(gender: Gender): Specification<ProfileEntity> =
            Specification<ProfileEntity> { root, cq, cb ->
                requireNotNull(cq).orderBy(cb.asc(root.get<Long>("id")))
                cb.equal(root.get<Char>("genderCode"), gender.code)
            }

        private fun purposesIn(purposes: Set<Purpose>): Specification<ProfileEntity> =
            Specification<ProfileEntity> { root, _, cb ->
                val masks = purposes.map { purpose ->
                    cb.equal(
                        cb.function("BITAND", Int::class.java, root.get<Int>("purposeMask"), cb.literal(purpose.code)),
                        purpose.code
                    )
                }
                cb.or(*masks.toTypedArray())
            }

        private fun locationIs(countryCode: String): Specification<ProfileEntity> =
            Specification<ProfileEntity> { root, _, cb ->
                cb.equal(root.get<Char>("countryId"), countryCode)
            }


        fun search(search: ProfileSearchContext): Specification<ProfileEntity> =
            genderIs(search.gender)
                .let { spec ->
                    spec.takeIf { search.purposes.isNotEmpty() }?.and(purposesIn(search.purposes)) ?: spec
                }
                .let { spec ->
                    search.countryId?.let { location -> spec.and(locationIs(location)) } ?: spec
                }
    }
}

