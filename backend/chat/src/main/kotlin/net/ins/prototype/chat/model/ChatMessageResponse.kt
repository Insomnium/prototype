package net.ins.prototype.chat.model

import net.ins.prototype.chat.socket.auth.P2pConstants

data class ChatMessageResponse(
    val messages: List<ChatMessage>,
) {

    val userMessages: List<ChatMessage>
        get() = messages.filterNot { it.sender == P2pConstants.ADMIN_SENDER_ID }
}

data class ChatMessage(
    val content: String,
    val sender: String,
    val chatRoomId: String,
)
