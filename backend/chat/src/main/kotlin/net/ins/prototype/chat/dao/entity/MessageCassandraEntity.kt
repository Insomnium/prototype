package net.ins.prototype.chat.dao.entity

import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.io.Serializable
import java.time.Instant

@Table("p2p_room")
data class MessageCassandraEntity(
    @PrimaryKey
    val id: P2pMessagePk,
    @Column("created_at")
    val createdAt: Instant,
    @Column("modified_at")
    val modifiedAt: Instant,
    @Column("content")
    val content: String,
    @Column("sender_id")
    val senderId: String,
    @Column("receiver_id")
    val receiverId: String,
    @Column("is_delivered")
    var isDelivered: Boolean,
    @Column("is_read")
    var isRead: Boolean,
)

@PrimaryKeyClass
data class P2pMessagePk(
    @PrimaryKeyColumn(name = "room_id", type = PrimaryKeyType.PARTITIONED)
    val roomId: String,
    @PrimaryKeyColumn(name = "message_id", type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    val messageId: String,
) : Serializable
