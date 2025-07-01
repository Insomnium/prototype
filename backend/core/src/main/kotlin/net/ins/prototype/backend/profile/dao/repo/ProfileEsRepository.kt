package net.ins.prototype.backend.profile.dao.repo

import net.ins.prototype.backend.profile.dao.model.ProfileEsEntity
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

interface ProfileEsRepository : ElasticsearchRepository<ProfileEsEntity, String> {

    fun findByDbId(dbId: Long): ProfileEsEntity?
}
