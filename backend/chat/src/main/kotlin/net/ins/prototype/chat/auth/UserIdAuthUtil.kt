package net.ins.prototype.chat.auth

import net.ins.prototype.chat.auth.exception.MissingSenderHeaderException
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.messaging.simp.stomp.StompHeaderAccessor

object P2pHeaders {
    const val SENDER = "X-sender-id"
    const val RECEIVER = "X-receiver-id"
}

val StompHeaderAccessor.senderId: String
    get() = getNativeHeader(P2pHeaders.SENDER)?.first() ?: throw MissingSenderHeaderException()

val SimpMessageHeaderAccessor.receiverId: String
    get() = getNativeHeader(P2pHeaders.RECEIVER)?.first() ?: throw MissingSenderHeaderException()

val SimpMessageHeaderAccessor.senderId: String
    get() = getNativeHeader(P2pHeaders.SENDER)?.first() ?: throw MissingSenderHeaderException()
