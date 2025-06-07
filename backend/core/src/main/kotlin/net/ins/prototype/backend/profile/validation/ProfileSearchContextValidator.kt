package net.ins.prototype.backend.profile.validation

import net.ins.prototype.backend.common.exception.ContextValidationException
import net.ins.prototype.backend.common.exception.EntityNotFoundException
import net.ins.prototype.backend.common.validation.Validator
import net.ins.prototype.backend.profile.dao.repo.ProfileRepository
import net.ins.prototype.backend.profile.service.context.ProfileSearchContext
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class ProfileSearchContextValidator(
    private val profileRepository: ProfileRepository,
) : Validator<ProfileSearchContext>() {

    override fun performValidation(source: ProfileSearchContext) {
        val user = profileRepository.findByIdOrNull(source.userId) ?: throw EntityNotFoundException("No user found with id: ${source.userId}")
        if (user.gender == source.gender) {
            throw ContextValidationException("search.invalid", "Homosexual search is forbidden")
        }
    }
}
