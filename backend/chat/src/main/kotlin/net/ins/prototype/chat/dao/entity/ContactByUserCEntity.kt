package net.ins.prototype.chat.dao.entity

import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.Table

@Table("contacts_by_user")
data class ContactByUserCEntity(
    @PrimaryKey("user_id")
    val userId: String,
    @field:Column("contact_id")
    val contactId: String,
    @field:Column("is_blocked")
    val blocked: Boolean,
    @field:Column("is_favourite")
    val favourite: Boolean,
    @field:Column("tags")
    val tags: Set<String>,
)
