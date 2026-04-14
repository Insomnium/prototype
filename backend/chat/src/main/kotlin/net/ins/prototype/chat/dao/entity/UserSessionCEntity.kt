package net.ins.prototype.chat.dao.entity

import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant

@Table("user_session")
data class UserSessionCEntity(
    @PrimaryKey("user_id")
    val id: String,
    @field:Column("instance_id")
    val instanceId: String,
    @field:Column("created_at")
    val createdAt: Instant,
)
