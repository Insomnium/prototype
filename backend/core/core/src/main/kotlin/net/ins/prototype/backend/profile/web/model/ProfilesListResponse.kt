package net.ins.prototype.backend.profile.web.model

import net.ins.prototype.backend.profile.model.Profile

data class ProfilesListResponse(
    val profiles: List<Profile>,
)
