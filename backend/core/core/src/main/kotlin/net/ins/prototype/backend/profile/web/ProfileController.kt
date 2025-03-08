package net.ins.prototype.backend.profile.web

import net.ins.prototype.backend.profile.service.ProfileService
import net.ins.prototype.backend.profile.web.converter.ProfileResponseConverter
import net.ins.prototype.backend.profile.web.model.ProfileResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("v1/profiles")
class ProfileController(
    private val profileService: ProfileService,
    private val profileResponseConverter: ProfileResponseConverter,
) {

    @GetMapping
    fun list(): ProfileResponse = ProfileResponse(profiles = profileService.findAll().map(profileResponseConverter::convert))
}
