package net.ins.prototype.backend.image.service.impl

import io.minio.BucketExistsArgs
import io.minio.MakeBucketArgs
import io.minio.MinioClient
import io.minio.PutObjectArgs
import net.ins.prototype.backend.conf.AppProperties
import net.ins.prototype.backend.image.service.ObjectStorageService
import net.ins.prototype.backend.image.service.ObjectStorageService.ObjectStorageEntity
import net.ins.prototype.common.logger
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
class ObjectStorageServiceImpl(
    private val minioClient: MinioClient,
    appProperties: AppProperties,
) : ObjectStorageService {

    private val bucket: String = appProperties.objectStorage.photoBucket
    private val rootFolder: String = appProperties.objectStorage.profilePhotoFolder
    private val cdnBaseUrl: String = appProperties.objectStorage.cdnBaseUrl

    override fun savePhoto(profileId: Long, binary: MultipartFile): ObjectStorageEntity {
        preservePhotoBucket()

        val fileName = requireNotNull(binary.originalFilename) {
            "File name can not be empty"
        }
        val extension = fileName.substringAfterLast(".")
        val internalFileName = "${UUID.randomUUID()}.$extension"
        val profileFolder = "$rootFolder/$profileId"

        minioClient.putObject(
            PutObjectArgs.builder()
                .bucket(bucket)
                .`object`("$profileFolder/${internalFileName}")
                .contentType(binary.contentType) // TODO: validate files, being uploaded. Limit by size, filter by content-type, anti-malware(?)
                .stream(binary.inputStream, binary.size, -1)
                .build()
        ).also {
            logger.debug("Profile photo persisted: {}", it)
        }

        return ObjectStorageEntity(
            folder = profileFolder,
            internalFileName = internalFileName,
            extension = extension,
            cdnBaseUrl = cdnBaseUrl,
        )
    }

    private fun preservePhotoBucket() {
        if (!minioClient.bucketExists(bucket.toBucketRequest())) {
            logger.warn("Missing $bucket bucket")
            minioClient.makeBucket(bucket.toMakeBucketRequest())
            logger.info("Bucket $bucket successfully created")
        }
    }

    companion object {

        fun String.toBucketRequest(): BucketExistsArgs = BucketExistsArgs.builder()
            .bucket(this)
            .build()

        fun String.toMakeBucketRequest(): MakeBucketArgs = MakeBucketArgs.builder()
            .bucket(this)
            .build()
    }
}