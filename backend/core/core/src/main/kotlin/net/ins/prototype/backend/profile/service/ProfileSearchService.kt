package net.ins.prototype.backend.profile.service

import net.ins.prototype.backend.profile.dao.model.ProfileEntity
import net.ins.prototype.backend.profile.dao.model.ProfileEsEntity

interface ProfileSearchService {

    fun findAll(search: ProfileSearchContext): List<ProfileEntity>

    fun index(profileEsEntity: ProfileEsEntity)
}
