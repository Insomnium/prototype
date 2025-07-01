package net.ins.prototype.backend.profile.service.impl

import jakarta.transaction.Transactional
import net.ins.prototype.backend.common.exception.EntityNotFoundException
import net.ins.prototype.backend.profile.converter.ProfileContextToProfileEntityConverter
import net.ins.prototype.backend.profile.converter.ProfileEntityUpdateFromContextConverter
import net.ins.prototype.backend.profile.dao.model.ProfileEntity
import net.ins.prototype.backend.profile.dao.repo.ProfileRepository
import net.ins.prototype.backend.profile.event.ProfileEventContext
import net.ins.prototype.backend.profile.event.ProfileEventPublisher
import net.ins.prototype.backend.profile.event.ProfileEventType
import net.ins.prototype.backend.profile.service.ProfileService
import net.ins.prototype.backend.profile.service.context.NewProfileContext
import net.ins.prototype.backend.profile.service.context.ProfileSearchContext
import net.ins.prototype.backend.profile.service.context.UpdateProfileContext
import net.ins.prototype.backend.profile.validation.NewProfileContextValidator
import net.ins.prototype.backend.profile.validation.UpdateProfileContextValidator
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ProfileServiceImpl(
    private val profileRepository: ProfileRepository,
    private val newProfileContextValidator: NewProfileContextValidator,
    private val updateProfileContextValidator: UpdateProfileContextValidator,
    private val profileContextToEntityConverter: ProfileContextToProfileEntityConverter,
    private val profileEventPublisher: ProfileEventPublisher,
    private val profileEntityUpdateConverter: ProfileEntityUpdateFromContextConverter,
) : ProfileService {

    @Transactional
    override fun create(context: NewProfileContext): Long {
        newProfileContextValidator.validate(context)
        val dbProfile = profileRepository.save(profileContextToEntityConverter.convert(context))
        profileEventPublisher.publish(ProfileEventContext(dbProfile, ProfileEventType.CREATED))
        return requireNotNull(dbProfile.id)
    }

    override fun getById(id: Long): ProfileEntity =
        profileRepository.findByIdOrNull(id) ?: throw EntityNotFoundException("No profile found by id: $id")

    override fun getByIds(ids: List<Long>): List<ProfileEntity> = profileRepository.findAllById(ids)

    override fun findAll(search: ProfileSearchContext): List<ProfileEntity> = profileRepository.findAll(ProfileRepository.search(search))

    override fun update(context: UpdateProfileContext): ProfileEntity {
        updateProfileContextValidator.validate(context)
        val updatedDbProfile = profileRepository.save(profileEntityUpdateConverter.convert(getById(context.id) to context))
        profileEventPublisher.publish(ProfileEventContext(updatedDbProfile, ProfileEventType.UPDATED))
        return updatedDbProfile
    }

    override fun markIndexed(id: Long, indexId: String) {
        profileRepository.setIndexedAt(id, indexId, LocalDateTime.now())
    }
}
