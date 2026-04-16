package net.ins.prototype.backend.image.service

import net.ins.prototype.backend.image.dao.model.ImageEntity
import org.springframework.web.multipart.MultipartFile

interface ImageService {

    fun saveImage(userId: Long, file: MultipartFile, profileId: Long): ImageEntity

    fun getAllByProfileId(profileId: Long): List<ImageEntity>

    fun delete(id: Long, profileId: Long)
}
