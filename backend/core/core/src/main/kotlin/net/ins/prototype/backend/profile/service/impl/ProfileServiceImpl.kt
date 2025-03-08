package net.ins.prototype.backend.profile.service.impl

import net.ins.prototype.backend.profile.dao.model.ProfileEntity
import net.ins.prototype.backend.profile.dao.repo.ProfileRepository
import net.ins.prototype.backend.profile.service.ProfileService
import org.springframework.stereotype.Service

@Service
class ProfileServiceImpl(
    private val profileRepository: ProfileRepository,
) : ProfileService {

    override fun findAll(): List<ProfileEntity> = profileRepository.findAll()
}
