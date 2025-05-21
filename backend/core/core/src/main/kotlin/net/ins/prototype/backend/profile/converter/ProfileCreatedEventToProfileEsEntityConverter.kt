package net.ins.prototype.backend.profile.converter

import net.ins.prototype.backend.profile.dao.model.ProfileEsEntity
import net.ins.prototype.backend.profile.dao.model.PurposeEsSubEntity
import net.ins.prototype.backend.profile.event.ProfileCreatedEvent
import net.ins.prototype.backend.profile.event.ProfileEvent
import net.ins.prototype.backend.profile.model.Purpose
import org.springframework.stereotype.Component

//@Component
//class ProfileCreatedEventToProfileEsEntityConverter : Converter<ProfileEvent, ProfileEsEntity> {
//
//    override fun convert(source: ProfileEvent): ProfileEsEntity {
//        when(source) {
//            is ProfileCreatedEvent -> toProfileCreatedEsEntity(source)
//            else -> throw IllegalArgumentException("Unknown event: $source")
//        }
//    }
//
//    private fun toProfileCreatedEsEntity(source: ProfileCreatedEvent): ProfileEsEntity {
//        val purposes = Purpose.unmask(source.purposes)
//        return ProfileEsEntity(
//            dbId = requireNotNull(source.dbId),
//            gender = source.gender,
////            birth = source.birth,
//            countryId = source.countryId,
//            purpose = PurposeEsSubEntity(
//                dating = Purpose.DATING in purposes,
//                sexting = Purpose.SEXTING in purposes,
//                relationships = Purpose.RELATIONSHIPS in purposes,
//            ),
//        )
//    }
//}
