package net.ins.prototype.backend.profile.web.model

import jakarta.validation.constraints.NotNull
import net.ins.prototype.backend.profile.model.Gender
import net.ins.prototype.backend.profile.model.Purpose

data class ProfileRequest(
    @field:NotNull(message = "gender can't be null or empty")
    val gender: Gender?,
    val purposes: Set<Purpose>? = emptySet(),
    val countryId: String? = null,
)
