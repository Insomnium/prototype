package net.ins.prototype.backend.profile.service

import net.ins.prototype.backend.profile.dao.model.ProfileEntity

interface ProfileService {

    fun findAll(search: ProfileSearchContext): List<ProfileEntity>

    fun create(newProfile: NewProfileContext): Long
}
