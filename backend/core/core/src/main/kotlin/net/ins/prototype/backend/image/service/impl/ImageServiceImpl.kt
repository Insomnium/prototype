package net.ins.prototype.backend.image.service.impl

import net.ins.prototype.backend.conf.AppProperties
import net.ins.prototype.backend.image.dao.model.ImageEntity
import net.ins.prototype.backend.image.dao.repo.ImageRepository
import net.ins.prototype.backend.image.service.ImageService
import net.ins.prototype.backend.profile.service.ProfileService
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Path
import java.util.UUID
import kotlin.io.path.absolutePathString

@Service
class ImageServiceImpl(
    private val appProperties: AppProperties,
    private val imageRepository: ImageRepository,
    private val profileService: ProfileService,
) : ImageService {

    override fun saveImage(file: MultipartFile, profileId: Long): ImageEntity {
        profileService.getById(profileId)
        val profileFolder = preserveProfileFolder(profileId)
        val internalFileName = saveBinary(profileFolder, file)
        return imageRepository.save(
            ImageEntity(
                id = null,
                profileId = profileId,
                folderUri = profileFolder.absolutePathString(),
                cdnUri = "${appProperties.images.cdnBaseUri}/$profileId/$internalFileName",
                internalFileName = internalFileName,
                primary = !imageRepository.existsByProfileId(profileId),
            )
        )
    }

    private fun preserveProfileFolder(profileId: Long): Path {
        val profileFolder = Path.of(appProperties.images.fsBaseUri, profileId.toString())
        if (!Files.exists(profileFolder)) {
            Files.createDirectories(profileFolder)
        }
        return profileFolder
    }

    private fun saveBinary(folder: Path, file: MultipartFile): String {
        val extension = requireNotNull(file.originalFilename).substringAfterLast(".")
        val internalFileName = "${UUID.randomUUID()}.$extension"
        val destination = folder.resolve(internalFileName)
        file.transferTo(destination)
        return internalFileName
    }
}
