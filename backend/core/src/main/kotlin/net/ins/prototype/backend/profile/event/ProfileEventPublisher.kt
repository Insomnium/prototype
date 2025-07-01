package net.ins.prototype.backend.profile.event

import net.ins.prototype.backend.common.event.AbstractEventPublisher
import net.ins.prototype.backend.conf.Topics
import net.ins.prototype.backend.profile.dao.model.ProfileEntity
import net.ins.prototype.backend.profile.model.Purpose
import org.springframework.stereotype.Component

@Component
class ProfileEventPublisher : AbstractEventPublisher<ProfileEventContext, Long, ProfileEvent>() {

    override fun payload(source: ProfileEventContext): ProfileEvent = when (source.type) {
        ProfileEventType.CREATED -> ProfileCreatedEvent(
            dbId = requireNotNull(source.profile.id),
            gender = source.profile.gender,
            birth = source.profile.birth,
            countryId = source.profile.countryId,
            purposes = Purpose.unmask(source.profile.purposeMask),
        )
        ProfileEventType.UPDATED -> ProfileUpdatedEvent(
            dbId = requireNotNull(source.profile.id),
            birth = source.profile.birth,
            gender = source.profile.gender,
            countryId = source.profile.countryId,
            purposes = Purpose.unmask(source.profile.purposeMask),
        )
        else -> TODO("Add support for deletion events")
    }

    override fun key(source: ProfileEventContext): Long = requireNotNull(source.profile.id)

    override fun topic(topics: Topics): String = topics.profiles.name
}
