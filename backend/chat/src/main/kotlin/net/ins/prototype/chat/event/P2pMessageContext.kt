package net.ins.prototype.chat.event

data class P2pMessageContext(
    val senderId: String,
    val receiverId: String,
    val content: String,
)
