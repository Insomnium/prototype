package net.ins.prototype.backend.profile.converter

import net.ins.prototype.backend.common.converter.Converter
import net.ins.prototype.backend.profile.dao.model.ProfileEntity
import net.ins.prototype.backend.profile.model.calculateMask
import net.ins.prototype.backend.profile.service.NewProfileContext
import org.springframework.stereotype.Component

@Component
class ProfileContextToProfileEntityConverter : Converter<NewProfileContext, ProfileEntity> {

    override fun convert(source: NewProfileContext): ProfileEntity = ProfileEntity(
        title = source.title,
        birth = source.birth,
        gender = source.gender,
        countryId = source.countryId,
        purposeMask = source.purposes.calculateMask(),
    )
}
