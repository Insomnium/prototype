package net.ins.prototype.backend.profile.event

import com.github.avrokotlin.avro4k.serializer.LocalDateSerializer
import kotlinx.serialization.Serializable
import net.ins.prototype.backend.common.meta.NoArgConstructor
import net.ins.prototype.backend.profile.model.Gender
import net.ins.prototype.backend.profile.model.Purpose
import java.time.LocalDate

@NoArgConstructor
@Serializable
data class ProfileCreatedEvent(
    val dbId: Long,
    val gender: Gender,
    @Serializable(with = LocalDateSerializer::class)
    val birth: LocalDate,
    val countryId: String,
    val purposes: Set<Purpose> = emptySet(),
) : ProfileEvent {

    companion object {
        const val SUBJECT = "net.ins.prototype.backend.profile.event.ProfileCreatedEvent"
    }
}
