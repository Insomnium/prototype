package net.ins.prototype.backend.image.service

import org.springframework.web.multipart.MultipartFile

interface ObjectStorageService {

    fun savePhoto(profileId: Long, binary: MultipartFile): ObjectStorageEntity

    data class ObjectStorageEntity(
        val folder: String,
        val internalFileName: String,
        val extension: String,
        val cdnBaseUrl: String,
    ) {

        val fullCdnUrl: String
            get() = "$cdnBaseUrl/$folder/$internalFileName"
    }
}