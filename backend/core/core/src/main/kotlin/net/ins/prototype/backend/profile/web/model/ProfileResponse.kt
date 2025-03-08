package net.ins.prototype.backend.profile.web.model

import net.ins.prototype.backend.profile.model.Profile

data class ProfileResponse(
    val profiles: List<Profile>
)
