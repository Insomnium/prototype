package net.ins.prototype.backend.profile.model

import java.time.LocalDate

data class Profile(
    val id: Long,
    val title: String,
    val birth: LocalDate,
    val gender: Gender,
)
