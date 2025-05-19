package net.ins.prototype.backend.profile.event

import net.ins.prototype.backend.common.event.AbstractEventPublisher
import net.ins.prototype.backend.conf.Topics
import net.ins.prototype.backend.profile.dao.model.ProfileEntity
import net.ins.prototype.backend.profile.model.Purpose
import org.springframework.stereotype.Component

@Component
class ProfileEventPublisher : AbstractEventPublisher<ProfileEntity, Long, ProfileEvent>() {

    override fun payload(source: ProfileEntity): ProfileEvent = ProfileCreatedEvent(
        dbId = requireNotNull(source.id),
        gender = source.gender.code.toString(),
        birth = source.birth,
        countryId = source.countryId,
        purposes = Purpose.unmask(source.purposeMask),
    )

    override fun key(source: ProfileEntity): Long = requireNotNull(source.id)

    override fun topic(topics: Topics): String = topics.profiles.name
}
