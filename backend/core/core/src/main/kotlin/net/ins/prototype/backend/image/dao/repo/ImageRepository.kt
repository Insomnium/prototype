package net.ins.prototype.backend.image.dao.repo

import net.ins.prototype.backend.image.dao.model.ImageEntity
import org.springframework.data.repository.CrudRepository

interface ImageRepository : CrudRepository<ImageEntity, Long> {

    fun existsByProfileId(profileId: Long): Boolean
}
