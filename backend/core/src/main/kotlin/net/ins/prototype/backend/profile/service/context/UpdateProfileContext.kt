package net.ins.prototype.backend.profile.service.context

import net.ins.prototype.backend.profile.model.Gender
import net.ins.prototype.backend.profile.model.Purpose
import java.time.LocalDate

data class UpdateProfileContext(
    val id: Long,
    val title: String? = null,
    val birth: LocalDate? = null,
    val gender: Gender? = null,
    val countryId: String? = null,
    val purposes: Set<Purpose>? = null,
)
