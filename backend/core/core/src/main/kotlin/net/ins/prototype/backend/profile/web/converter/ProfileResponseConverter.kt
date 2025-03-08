package net.ins.prototype.backend.profile.web.converter

import net.ins.prototype.backend.profile.dao.model.ProfileEntity
import net.ins.prototype.backend.profile.model.Profile
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class ProfileResponseConverter : Converter<ProfileEntity, Profile> {

    override fun convert(source: ProfileEntity): Profile = Profile(
        requireNotNull(source.id),
        source.title,
        source.birth,
        source.gender,
    )
}
