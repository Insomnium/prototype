package net.ins.prototype.backend.profile.web.model

import net.ins.prototype.backend.profile.model.Gender
import net.ins.prototype.backend.profile.model.Interest

data class ProfileRequest(
    val gender: Gender,
    val purposes: List<Interest> = emptyList(),
)
