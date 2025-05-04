package net.ins.prototype.backend.profile.converter

import net.ins.prototype.backend.profile.dao.model.ProfileEntity
import net.ins.prototype.backend.profile.dao.model.ProfileEsEntity
import net.ins.prototype.backend.profile.dao.model.PurposeEsSubEntity
import net.ins.prototype.backend.profile.model.Purpose
import org.springframework.stereotype.Component

@Component
class ProfileEntityToProfileEsEntityConverter : Converter<ProfileEntity, ProfileEsEntity> {

    override fun convert(source: ProfileEntity): ProfileEsEntity {
        val purposes = Purpose.unmask(source.purposeMask)
        return ProfileEsEntity(
            dbId = requireNotNull(source.id),
            gender = source.gender.code.toString(),
            birth = source.birth,
            countryId = source.countryId,
            purpose = PurposeEsSubEntity(
                dating = Purpose.DATING in purposes,
                sexting = Purpose.SEXTING in purposes,
                relationships = Purpose.RELATIONSHIPS in purposes,
            ),
        )
    }
}