package net.ins.prototype.backend.profile.web

import jakarta.validation.Valid
import net.ins.prototype.backend.image.service.ImageService
import net.ins.prototype.backend.common.web.model.EntityIdResponse
import net.ins.prototype.backend.common.web.model.EntityListResponse
import net.ins.prototype.backend.common.web.model.EntityResponse
import net.ins.prototype.backend.image.web.converter.ImageResponseConverter
import net.ins.prototype.backend.image.web.model.Image
import net.ins.prototype.backend.profile.model.Profile
import net.ins.prototype.backend.profile.service.context.NewProfileContext
import net.ins.prototype.backend.profile.service.context.ProfileSearchContext
import net.ins.prototype.backend.profile.service.ProfileIndexService
import net.ins.prototype.backend.profile.service.ProfileService
import net.ins.prototype.backend.profile.service.context.UpdateProfileContext
import net.ins.prototype.backend.profile.web.converter.ProfileResponseConverter
import net.ins.prototype.backend.profile.web.model.CreateProfileRequest
import net.ins.prototype.backend.profile.web.model.ProfileSearchRequest
import net.ins.prototype.backend.profile.web.model.UpdateProfileRequest
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
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
    private val profileIndexService: ProfileIndexService,
    private val profileResponseConverter: ProfileResponseConverter,
    private val imageResponseConverter: ImageResponseConverter,
) {

    @GetMapping
    fun list(
        @RequestHeader("x-user-id") userId: Long,
        @Valid @ModelAttribute request: ProfileSearchRequest,
    ): EntityListResponse<Profile> = EntityListResponse(
        results = profileIndexService.findAll(
            ProfileSearchContext(
                userId = userId,
                gender = requireNotNull(request.gender),
                purposes = requireNotNull(request.purposes),
                countryId = request.countryId
            )
        ).map(profileResponseConverter::convert)
    )

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody request: CreateProfileRequest): EntityIdResponse<Long> = EntityIdResponse(
        profileService.create(
            NewProfileContext(
                title = request.title,
                birth = request.birth,
                gender = request.gender,
                countryId = request.countryId,
                purposes = request.purposes,
            )
        )
    )

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun update(
        @PathVariable("id") id: Long,
        @Valid @RequestBody request: UpdateProfileRequest, // TODO: validate id == x-user-id
    ): EntityResponse<Profile> = EntityResponse(
        profileService.update(
            UpdateProfileContext(
                id = id,
                title = request.title,
                birth = request.birth,
                gender = request.gender,
                countryId = request.countryId,
                purposes = request.purposes,
            )
        ).let(profileResponseConverter::convert)
    )

    @PostMapping("/{id}/images")
    fun uploadPhoto(
        @RequestParam("file") file: MultipartFile,
        @PathVariable("id") profileId: Long,
    ): EntityIdResponse<Long> = EntityIdResponse(requireNotNull(imageService.saveImage(file, profileId).id))

    @GetMapping("/{id}/images")
    fun listProfilePhotos(@PathVariable("id") profileId: Long): EntityListResponse<Image> =
        EntityListResponse(imageService.getAllByProfileId(profileId).map(imageResponseConverter::convert))

    @DeleteMapping("/{id}/images/{imageId}")
    fun deleteImage(@PathVariable("id") profileId: Long, @PathVariable("imageId") imageId: Long) {
        imageService.delete(imageId, profileId)
    }
}
