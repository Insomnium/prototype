package net.ins.prototype.chat.service.impl

import net.ins.prototype.chat.event.P2pMessageContext
import net.ins.prototype.chat.service.IdGenerator
import org.springframework.stereotype.Component

@Component
class ChatIdGenerator : IdGenerator<String, P2pMessageContext> {

    override fun generateId(context: P2pMessageContext): String {
        val attendeeIds = sortedSetOf(context.senderId, context.receiverId)
        return "p2p_${attendeeIds.first()}_${attendeeIds.last()}"
    }
}
