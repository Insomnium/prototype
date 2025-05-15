package net.ins.prototype.backend.profile.event

import net.ins.prototype.backend.profile.model.Purpose
import java.time.LocalDate

data class ProfileCreatedEvent(
    val dbId: Long,
    val gender: String,
    val birth: LocalDate,
    val countryId: String,
    val purposes: Set<Purpose>,
) : ProfileEvent
