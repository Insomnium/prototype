package net.ins.prototype.backend.profile.validation

import net.ins.prototype.backend.common.exception.ContextValidationException
import net.ins.prototype.backend.common.exception.EntityNotFoundException
import net.ins.prototype.backend.common.validation.Validator
import net.ins.prototype.backend.profile.dao.repo.ProfileRepository
import net.ins.prototype.backend.profile.service.context.UpdateProfileContext
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class UpdateProfileContextValidator(
    private val profileRepository: ProfileRepository,
) : Validator<UpdateProfileContext>() {

    override fun performValidation(source: UpdateProfileContext) {
        val profile = profileRepository.findByIdOrNull(source.id)
            ?: throw EntityNotFoundException("No profile found by id: ${source.id}")

        if (source.gender != profile.gender) {
            throw ContextValidationException(
                "profile.update.invalid",
                "No gender change allowed",
            )
        }
    }
}
