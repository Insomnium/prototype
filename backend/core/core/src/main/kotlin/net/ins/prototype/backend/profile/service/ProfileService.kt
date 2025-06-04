package net.ins.prototype.backend.profile.service

import net.ins.prototype.backend.profile.dao.model.ProfileEntity
import net.ins.prototype.backend.profile.service.context.NewProfileContext

interface ProfileService {

    fun create(newProfile: NewProfileContext): Long

    fun getById(id: Long): ProfileEntity
}
