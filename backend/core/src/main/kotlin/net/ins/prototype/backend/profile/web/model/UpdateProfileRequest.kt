package net.ins.prototype.backend.profile.web.model

import net.ins.prototype.backend.profile.model.Gender
import net.ins.prototype.backend.profile.model.Purpose
import java.time.LocalDate

data class UpdateProfileRequest(
    val title: String,
    val birth: LocalDate,
    val gender: Gender,
    val purposes: Set<Purpose>,
    val countryId: String,
)
