package net.ins.prototype.backend.profile.converter

import net.ins.prototype.backend.common.converter.Converter
import net.ins.prototype.backend.profile.dao.model.ProfileEntity
import net.ins.prototype.backend.profile.model.Purpose
import net.ins.prototype.backend.profile.service.context.UpdateProfileContext
import org.springframework.stereotype.Component

@Component
class ProfileEntityUpdateFromContextConverter : Converter<Pair<ProfileEntity, UpdateProfileContext>, ProfileEntity> {

    override fun convert(source: Pair<ProfileEntity, UpdateProfileContext>): ProfileEntity {
        val entity = source.first
        val context = source.second
        return ProfileEntity(
            id = requireNotNull(entity.id),
            title = context.title ?: entity.title,
            birth = context.birth ?: entity.birth,
            gender = entity.gender,
            countryId = context.countryId ?: entity.countryId,
            purposes = context.purposes ?: Purpose.unmask(entity.purposeMask),
            createdAt = entity.createdAt,
            lastIndexedAt = entity.lastIndexedAt,
        )
    }
}
