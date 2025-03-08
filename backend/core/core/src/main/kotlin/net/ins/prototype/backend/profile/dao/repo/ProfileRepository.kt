package net.ins.prototype.backend.profile.dao.repo

import net.ins.prototype.backend.profile.dao.model.ProfileEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ProfileRepository : JpaRepository<ProfileEntity, Long> {
}
