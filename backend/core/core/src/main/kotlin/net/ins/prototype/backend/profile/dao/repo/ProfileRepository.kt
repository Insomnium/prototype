package net.ins.prototype.backend.profile.dao.repo

import jakarta.persistence.criteria.Join
import net.ins.prototype.backend.profile.dao.model.PurposeEntity
import net.ins.prototype.backend.profile.dao.model.ProfileEntity
import net.ins.prototype.backend.profile.model.Gender
import net.ins.prototype.backend.profile.model.Purpose
import net.ins.prototype.backend.profile.model.calculateMask
import net.ins.prototype.backend.profile.service.ProfileSearchContext
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface ProfileRepository : JpaRepository<ProfileEntity, Long>, JpaSpecificationExecutor<ProfileEntity> {

    companion object {

        private fun genderIs(gender: Gender): Specification<ProfileEntity> =
            Specification<ProfileEntity> { root, _, cb ->
                cb.equal(root.get<Char>("genderCode"), gender.code)
            }

        private fun purposesIn(purposes: Set<Purpose>): Specification<ProfileEntity> =
            Specification<ProfileEntity> { root, _, criteriaBuilder ->
                val interestJoin: Join<ProfileEntity, PurposeEntity> = root.join("interest")
                criteriaBuilder.equal(interestJoin.get<Int>("mask"), purposes.calculateMask())
            }


        fun search(search: ProfileSearchContext): Specification<ProfileEntity> =
            genderIs(search.gender)
                .let { spec ->
                    spec.takeIf { search.purposes.isNotEmpty() }?.and(purposesIn(search.purposes)) ?: spec
                }
    }
}

