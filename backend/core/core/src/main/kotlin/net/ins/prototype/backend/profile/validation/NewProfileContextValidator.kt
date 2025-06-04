package net.ins.prototype.backend.profile.validation

import net.ins.prototype.backend.common.validation.Validator
import net.ins.prototype.backend.geo.service.LocationService
import net.ins.prototype.backend.profile.service.context.NewProfileContext
import org.springframework.stereotype.Component

@Component
class NewProfileContextValidator(
    private val locationService: LocationService,
) : Validator<NewProfileContext>() {

    override fun performValidation(source: NewProfileContext) {
        locationService.getById(source.countryId)
    }
}
