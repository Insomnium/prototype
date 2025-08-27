package net.ins.prototype.chat.auth.interceptor

import net.ins.prototype.chat.auth.model.UnauthorizedPrincipal
import net.ins.prototype.chat.auth.model.UserIdPrincipal
import net.ins.prototype.chat.auth.senderId
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.stereotype.Component

@Component
class UserIdAuthChannelInterceptor : ChannelInterceptor {

    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*> {
        val accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor::class.java)!!
        if (accessor.command in listOf(StompCommand.CONNECT, StompCommand.SEND)) {
            accessor.user = kotlin.runCatching { UserIdPrincipal(accessor.senderId) }.getOrElse { UnauthorizedPrincipal() }
        }
        return message
    }
}
