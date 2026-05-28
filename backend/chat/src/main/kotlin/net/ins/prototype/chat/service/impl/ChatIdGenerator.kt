package net.ins.prototype.chat.service.impl

import net.ins.prototype.chat.event.P2pMessageContext
import net.ins.prototype.chat.model.ChatRoomId
import net.ins.prototype.chat.service.IdGenerator
import org.springframework.stereotype.Component

@Component
class ChatIdGenerator : IdGenerator<String, P2pMessageContext> {

    override fun generateId(context: P2pMessageContext): String = "p2p_${ChatRoomId(context.senderId, context.receiverId)}"
}
