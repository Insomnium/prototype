package net.ins.prototype.backend.profile.service.validator.impl

import net.ins.prototype.backend.common.exception.ContextValidationException
import net.ins.prototype.backend.geo.dao.repo.LocationRepository
import net.ins.prototype.backend.profile.service.NewProfileContext
import net.ins.prototype.backend.profile.service.validator.Validator
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class NewProfileContextValidator(
    private val locationRepository: LocationRepository,
) : Validator<NewProfileContext> {

    override fun validate(source: NewProfileContext) {
        locationRepository.findByIdOrNull(source.countryId)
            ?: throw ContextValidationException(
                "profile.validation.countryId",
                "No country found by id: ${source.countryId}"
            )
    }
}
