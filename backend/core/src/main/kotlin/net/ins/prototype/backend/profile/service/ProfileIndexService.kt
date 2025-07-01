package net.ins.prototype.backend.profile.service

import net.ins.prototype.backend.profile.dao.model.ProfileEntity
import net.ins.prototype.backend.profile.dao.model.ProfileEsEntity
import net.ins.prototype.backend.profile.event.ProfileCreatedEvent
import net.ins.prototype.backend.profile.event.ProfileUpdatedEvent
import net.ins.prototype.backend.profile.service.context.ProfileSearchContext

interface ProfileIndexService {

    fun findAll(search: ProfileSearchContext): List<ProfileEntity>

    fun index(profileCreatedEvent: ProfileCreatedEvent)

    fun updateIndex(profileUpdatedEvent: ProfileUpdatedEvent)
}
