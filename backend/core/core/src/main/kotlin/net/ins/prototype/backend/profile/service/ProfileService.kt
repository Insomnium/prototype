package net.ins.prototype.backend.profile.service

interface ProfileService {

    fun create(newProfile: NewProfileContext): Long
}
