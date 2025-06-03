package net.ins.prototype.backend.profile.web

import jakarta.validation.Valid
import net.ins.prototype.backend.image.service.ImageService
import net.ins.prototype.backend.profile.dao.model.EntityIdResponse
import net.ins.prototype.backend.profile.service.NewProfileContext
import net.ins.prototype.backend.profile.service.ProfileSearchContext
import net.ins.prototype.backend.profile.service.ProfileSearchService
import net.ins.prototype.backend.profile.service.ProfileService
import net.ins.prototype.backend.profile.web.converter.ProfileResponseConverter
import net.ins.prototype.backend.profile.web.model.NewProfileRequest
import net.ins.prototype.backend.profile.web.model.ProfileSearchRequest
import net.ins.prototype.backend.profile.web.model.ProfilesListResponse
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("v1/profiles")
@Validated
class ProfileController(
    private val profileService: ProfileService,
    private val imageService: ImageService,
    private val profileSearchService: ProfileSearchService,
    private val profileResponseConverter: ProfileResponseConverter,
) {

    @GetMapping
    fun list(@Valid @ModelAttribute request: ProfileSearchRequest): ProfilesListResponse = ProfilesListResponse(
        profiles = profileSearchService.findAll(
            ProfileSearchContext(gender = requireNotNull(request.gender), purposes = requireNotNull(request.purposes), countryId = request.countryId)
        ).map(profileResponseConverter::convert)
    )

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody request: NewProfileRequest): EntityIdResponse = EntityIdResponse(
        profileService.create(
            NewProfileContext(
                title = request.title,
                birth = request.birth,
                gender = request.gender,
                countryId = request.countryId,
                purposes = request.purposes,
            )
        ).toString()
    )

    @PostMapping("/{id}/images")
    fun uploadPhoto(
        @RequestParam("file") file: MultipartFile,
        @PathVariable("id") userId: Long,
    ): EntityIdResponse = EntityIdResponse(requireNotNull(imageService.saveImage(file, userId).id).toString())
}
