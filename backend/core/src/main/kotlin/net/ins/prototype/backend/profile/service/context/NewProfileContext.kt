package net.ins.prototype.backend.profile.service.context

import net.ins.prototype.backend.profile.model.Gender
import net.ins.prototype.backend.profile.model.Purpose
import java.time.LocalDate

data class NewProfileContext(
    val title: String,
    val birth: LocalDate,
    val gender: Gender,
    val countryId: String,
    val purposes: Set<Purpose>,
)
