package net.ins.prototype.backend.profile.validation

import net.ins.prototype.backend.common.exception.AccessDeniedException
import net.ins.prototype.backend.common.validation.Validator
import net.ins.prototype.backend.profile.service.context.ProfileModificationContext
import org.springframework.stereotype.Component

@Component
class ProfileModificationContextValidator : Validator<ProfileModificationContext>() {

    override fun performValidation(source: ProfileModificationContext) {
        if (source.userIdHeader != source.modifyingProfileId) {
            throw AccessDeniedException("Modification action on profile ${source.modifyingProfileId} is prohibited on behalf of user ${source.userIdHeader}")
        }
    }
}
