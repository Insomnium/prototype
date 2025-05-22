package net.ins.prototype.backend.profile.converter

import net.ins.prototype.backend.profile.dao.model.ProfileEsEntity
import net.ins.prototype.backend.profile.dao.model.PurposeEsSubEntity
import net.ins.prototype.backend.profile.event.ProfileCreatedEvent
import net.ins.prototype.backend.profile.event.ProfileEvent
import net.ins.prototype.backend.profile.model.Purpose
import org.springframework.stereotype.Component

@Component
class ProfileEventToProfileEsEntityConverter : Converter<ProfileEvent, ProfileEsEntity> {

    override fun convert(source: ProfileEvent): ProfileEsEntity {
        return when (source) {
            is ProfileCreatedEvent -> fromProfileCreatedEvent(source)
            else -> throw IllegalArgumentException("Unsupported event: $source")
        }
    }

    private fun fromProfileCreatedEvent(source: ProfileCreatedEvent): ProfileEsEntity {
        return ProfileEsEntity(
            dbId = requireNotNull(source.dbId),
            gender = source.gender,
            birth = source.birth,
            countryId = source.countryId,
            purpose = PurposeEsSubEntity(
                dating = Purpose.DATING in source.purposes,
                sexting = Purpose.SEXTING in source.purposes,
                relationships = Purpose.RELATIONSHIPS in source.purposes,
            ),
        )
    }
}
