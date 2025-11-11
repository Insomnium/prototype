package net.ins.prototype.chat.dao.repo

import com.datastax.oss.driver.api.core.DefaultConsistencyLevel
import net.ins.prototype.chat.dao.entity.ContactByUserCEntity
import org.springframework.data.cassandra.repository.Consistency
import org.springframework.data.repository.CrudRepository

interface UserContactsCRepo : CrudRepository<ContactByUserCEntity, String> {

    @Consistency(DefaultConsistencyLevel.LOCAL_QUORUM)
    fun findAllByUserId(userId: String): List<ContactByUserCEntity>
}
