package net.ins.prototype.chat.auth.interceptor

import net.ins.prototype.chat.auth.model.UnauthorizedPrincipal
import net.ins.prototype.chat.auth.model.UserIdPrincipal
import net.ins.prototype.chat.auth.senderId
import net.ins.prototype.chat.service.UserSessionService
import net.ins.prototype.common.logger
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.stereotype.Component

@Component
class UserIdAuthChannelInterceptor(
    private val userSessionService: UserSessionService,
) : ChannelInterceptor {

    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*> {
        val stompHeaderAccessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor::class.java)!!
        when (stompHeaderAccessor.command) {
            StompCommand.CONNECT -> {
                setMessageUserId(stompHeaderAccessor)
                registerSession(stompHeaderAccessor)
            }
            StompCommand.SEND -> {
                setMessageUserId(stompHeaderAccessor)
            }
            else -> { logger.debug("WS: {} command received", stompHeaderAccessor.command) }
        }
        return message
    }

    private fun setMessageUserId(accessor: StompHeaderAccessor) {
        accessor.user = runCatching { UserIdPrincipal(accessor.senderId) }.getOrElse { UnauthorizedPrincipal() }
    }

    private fun registerSession(accessor: StompHeaderAccessor) {
        userSessionService.registerSession(requireNotNull(accessor.user?.name))
    }
}
