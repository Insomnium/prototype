package net.ins.prototype.chat.dao.repo

import com.datastax.oss.driver.api.core.DefaultConsistencyLevel
import net.ins.prototype.chat.dao.entity.MessageCassandraEntity
import net.ins.prototype.chat.dao.entity.P2pMessagePk
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.cassandra.repository.Consistency
import org.springframework.data.cassandra.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface MessageCRepo : CassandraRepository<MessageCassandraEntity, P2pMessagePk> {

    @Query("""
        select *
        from p2p_room
        where room_id = :roomId
          and message_id < :afterMessageId
        order by message_id desc
        limit :pageSize
    """)
    @Consistency(DefaultConsistencyLevel.LOCAL_QUORUM)
    fun getMessages(
        @Param("roomId") roomId: String,
        @Param("afterMessageId") messageId: UUID,
        @Param("pageSize") pageSize: Int,
    ): List<MessageCassandraEntity>
}
