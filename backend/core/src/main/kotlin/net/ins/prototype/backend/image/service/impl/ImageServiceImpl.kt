package net.ins.prototype.backend.image.service.impl

import jakarta.transaction.Transactional
import net.ins.prototype.backend.common.exception.EntityNotFoundException
import net.ins.prototype.backend.conf.AppProperties
import net.ins.prototype.backend.image.dao.model.ImageEntity
import net.ins.prototype.backend.image.dao.repo.ImageRepository
import net.ins.prototype.backend.image.service.ImageService
import net.ins.prototype.backend.image.service.ObjectStorageService
import net.ins.prototype.backend.profile.service.ProfileService
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class ImageServiceImpl(
    private val appProperties: AppProperties,
    private val imageRepository: ImageRepository,
    private val profileService: ProfileService,
    private val objectStorage: ObjectStorageService,
) : ImageService {

    override fun saveImage(file: MultipartFile, profileId: Long): ImageEntity {
        // TODO: add meaningful configurable images per profile limit
        profileService.getById(profileId)
        val objectEntity = objectStorage.savePhoto(profileId, file)
        return imageRepository.save(
            ImageEntity(
                id = null,
                profileId = profileId,
                folderUri = objectEntity.folder,
                cdnUri = objectEntity.fullCdnUrl,
                internalFileName = objectEntity.internalFileName,
                extension = objectEntity.extension,
                primary = !imageRepository.existsByProfileId(profileId),
            )
        )
    }

    override fun getAllByProfileId(profileId: Long): List<ImageEntity> = imageRepository.findByProfileIdOrderById(profileId)

    @Transactional
    override fun delete(id: Long, profileId: Long) {
        profileService.getById(profileId)
        val profileImages = imageRepository.findByProfileIdOrderById(profileId)

        val imageForRemoval = profileImages.firstOrNull { it.id == id }
            ?: throw EntityNotFoundException("Profile image with $id found for profile $profileId")

        imageRepository.deleteById(id)

        if (imageForRemoval.primary) {
            profileImages.firstOrNull { it.id != id }?.run { imageRepository.save(this.apply { primary = true }) }
        }
    }
}
