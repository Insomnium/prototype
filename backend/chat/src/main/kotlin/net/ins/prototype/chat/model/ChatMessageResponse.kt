package net.ins.prototype.chat.model

data class ChatMessageResponse(
    val messages: List<ChatMessage>,
)

data class ChatMessage(
    val content: String,
    val sender: String,
)
