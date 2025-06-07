package net.ins.prototype.backend.profile.service

import net.ins.prototype.backend.profile.dao.model.ProfileEntity
import net.ins.prototype.backend.profile.dao.model.ProfileEsEntity
import net.ins.prototype.backend.profile.service.context.ProfileSearchContext

interface ProfileSearchService {

    fun findAll(search: ProfileSearchContext): List<ProfileEntity>

    fun index(profileEsEntity: ProfileEsEntity)
}
