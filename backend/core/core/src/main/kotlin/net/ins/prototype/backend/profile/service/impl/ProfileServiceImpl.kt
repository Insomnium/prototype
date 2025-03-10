package net.ins.prototype.backend.profile.service.impl

import jakarta.transaction.Transactional
import net.ins.prototype.backend.profile.dao.model.ProfileEntity
import net.ins.prototype.backend.profile.dao.model.ProfileEsEntity
import net.ins.prototype.backend.profile.dao.model.PurposeEsSubEntity
import net.ins.prototype.backend.profile.dao.repo.ProfileEsRepository
import net.ins.prototype.backend.profile.dao.repo.ProfileRepository
import net.ins.prototype.backend.profile.model.Purpose
import net.ins.prototype.backend.profile.model.calculateMask
import net.ins.prototype.backend.profile.service.NewProfileContext
import net.ins.prototype.backend.profile.service.ProfileSearchContext
import net.ins.prototype.backend.profile.service.ProfileService
import org.springframework.stereotype.Service

@Service
class ProfileServiceImpl(
    private val profileRepository: ProfileRepository,
    private val profileEsRepository: ProfileEsRepository,
) : ProfileService {

    override fun findAll(search: ProfileSearchContext): List<ProfileEntity> = profileRepository.findAll(ProfileRepository.search(search))

    @Transactional
    override fun create(newProfile: NewProfileContext): Long {
        val id = profileRepository.save(
            ProfileEntity(
                title = newProfile.title,
                birth = newProfile.birth,
                gender = newProfile.gender,
                purposeMask = newProfile.purposes.calculateMask()
            ) // FIXME: replace with converter?
        ).id

        profileEsRepository.save(
            ProfileEsEntity(
                dbId = requireNotNull(id),
                gender = newProfile.gender.code.toString(),
                birth = newProfile.birth,
                purpose = PurposeEsSubEntity(
                    dating = Purpose.DATING in newProfile.purposes,
                    sexting = Purpose.SEXTING in newProfile.purposes,
                    relationships = Purpose.RELATIONSHIPS in newProfile.purposes,
                )
            ) // TODO: replace with converter
        )

        return id
    }
}
