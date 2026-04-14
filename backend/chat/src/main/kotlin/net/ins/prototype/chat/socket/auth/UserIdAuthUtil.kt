package net.ins.prototype.chat.socket.auth

import net.ins.prototype.chat.socket.exception.MissingMandatoryHeaderException
import net.ins.prototype.chat.socket.exception.MissingReceiverHeaderException
import net.ins.prototype.chat.socket.exception.MissingSenderHeaderException
import net.ins.prototype.chat.socket.exception.MissingSessionException
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.NativeMessageHeaderAccessor

object P2pWsHeaders {
    const val SENDER = "X-sender-id"
    const val RECEIVER = "X-receiver-id"
}

object P2pWsQueryParams {
    const val USER_ID = "userId"
}

object P2pWsSessionAttributes {
    const val USER_ID = "userId"
}

object P2pConstants {
    const val ADMIN_SENDER_ID = "admin"
    const val P2P_MESSAGE_EVENT_SUBJECT = "net.ins.prototype.chat.event.P2pMessageEvent"
}

val <T : NativeMessageHeaderAccessor> T.senderId: String
    get() = getNativeHeader(P2pWsHeaders.SENDER) { MissingSenderHeaderException() }

val <T : NativeMessageHeaderAccessor> T.receiverId: String
    get() = getNativeHeader(P2pWsHeaders.RECEIVER) { MissingReceiverHeaderException() }

val <T: StompHeaderAccessor> T.userId: String
    get() = sessionAttributes?.get(P2pWsSessionAttributes.USER_ID) as? String ?: throw MissingSessionException()

private fun NativeMessageHeaderAccessor.getNativeHeader(
    key: String,
    onException: () -> MissingMandatoryHeaderException,
): String = getNativeHeader(key)?.first() ?: throw onException()
