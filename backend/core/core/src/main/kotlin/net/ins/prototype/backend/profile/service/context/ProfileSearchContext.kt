package net.ins.prototype.backend.profile.service.context

import net.ins.prototype.backend.profile.model.Gender
import net.ins.prototype.backend.profile.model.Purpose

data class ProfileSearchContext(
    val userId: Long,
    val gender: Gender,
    val purposes: Set<Purpose> = emptySet(),
    val countryId: String? = null,
)
