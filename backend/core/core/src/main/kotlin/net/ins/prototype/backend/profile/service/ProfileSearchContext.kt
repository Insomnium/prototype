package net.ins.prototype.backend.profile.service

import net.ins.prototype.backend.profile.model.Gender
import net.ins.prototype.backend.profile.model.Purpose

data class ProfileSearchContext(
    val gender: Gender,
    val purposes: Set<Purpose> = emptySet(),
)
