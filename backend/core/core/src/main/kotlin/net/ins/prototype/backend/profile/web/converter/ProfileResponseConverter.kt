package net.ins.prototype.backend.profile.web.converter

import net.ins.prototype.backend.profile.dao.model.ProfileEntity
import net.ins.prototype.backend.profile.model.Interest
import net.ins.prototype.backend.profile.model.Profile
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class ProfileResponseConverter : Converter<ProfileEntity, Profile> {

    override fun convert(source: ProfileEntity): Profile = Profile(
        id = requireNotNull(source.id),
        title = source.title,
        birth = source.birth,
        gender = source.gender,
        interests = Interest.unmask(source.interest.mask),
    )
}
