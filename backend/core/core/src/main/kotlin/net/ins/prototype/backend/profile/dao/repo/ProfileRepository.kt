package net.ins.prototype.backend.profile.dao.repo

import jakarta.persistence.criteria.Join
import net.ins.prototype.backend.profile.dao.model.PurposeEntity
import net.ins.prototype.backend.profile.dao.model.ProfileEntity
import net.ins.prototype.backend.profile.model.Gender
import net.ins.prototype.backend.profile.model.Purpose
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
            Specification<ProfileEntity> { root, _, cb ->
                val purposeJoin: Join<ProfileEntity, PurposeEntity> = root.join("purpose")
                val predicates = purposes.map { purpose ->
                    when (purpose) {
                        Purpose.DATING -> cb.isTrue(purposeJoin.get("dating"))
                        Purpose.SEXTING -> cb.isTrue(purposeJoin.get("sexting"))
                        Purpose.RELATIONSHIPS -> cb.isTrue(purposeJoin.get("relationships"))
                    }
                }
                cb.or(*predicates.toTypedArray())
            }


        fun search(search: ProfileSearchContext): Specification<ProfileEntity> =
            genderIs(search.gender)
                .let { spec ->
                    spec.takeIf { search.purposes.isNotEmpty() }?.and(purposesIn(search.purposes)) ?: spec
                }
    }
}

