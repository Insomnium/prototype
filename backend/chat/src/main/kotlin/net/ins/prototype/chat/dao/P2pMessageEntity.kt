package net.ins.prototype.chat.dao

import java.io.Serializable

@
data class P2pMessageEntity(
    val messageId: String,
)

@
data class P2pMessagePk(
    val roomId: String,
    val messageId: String,
) : Serializable
