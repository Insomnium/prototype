package net.ins.prototype.backend.profile.service.context

data class ProfileModificationContext(
    val userIdHeader: Long,
    val modifyingProfileId: Long,
)
