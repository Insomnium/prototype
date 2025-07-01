package net.ins.prototype.backend.profile.event

import net.ins.prototype.backend.profile.dao.model.ProfileEntity

data class ProfileEventContext(
    val profile: ProfileEntity,
    val type: ProfileEventType,
)

enum class ProfileEventType {
    CREATED, UPDATED, DELETED,
}
