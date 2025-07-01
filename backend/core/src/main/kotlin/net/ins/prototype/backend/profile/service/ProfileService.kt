package net.ins.prototype.backend.profile.service

import net.ins.prototype.backend.profile.dao.model.ProfileEntity
import net.ins.prototype.backend.profile.service.context.NewProfileContext
import net.ins.prototype.backend.profile.service.context.ProfileSearchContext
import net.ins.prototype.backend.profile.service.context.UpdateProfileContext

interface ProfileService {

    fun create(context: NewProfileContext): Long

    fun getById(id: Long): ProfileEntity

    fun getByIds(ids: List<Long>): List<ProfileEntity>

    fun findAll(search: ProfileSearchContext): List<ProfileEntity>

    fun update(context: UpdateProfileContext): ProfileEntity

    fun markIndexed(id: Long, indexId: String)
}
