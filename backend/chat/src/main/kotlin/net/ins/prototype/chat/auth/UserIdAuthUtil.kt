package net.ins.prototype.chat.auth

import net.ins.prototype.chat.auth.exception.MissingMandatoryHeaderException
import net.ins.prototype.chat.auth.exception.MissingReceiverHeaderException
import net.ins.prototype.chat.auth.exception.MissingSenderHeaderException
import org.springframework.messaging.support.NativeMessageHeaderAccessor

object P2pHeaders {
    const val SENDER = "X-sender-id"
    const val RECEIVER = "X-receiver-id"
}

val <T : NativeMessageHeaderAccessor> T.senderId: String
    get() = getNativeHeader(P2pHeaders.SENDER) { MissingSenderHeaderException() }

val <T : NativeMessageHeaderAccessor> T.receiverId: String
    get() = getNativeHeader(P2pHeaders.RECEIVER) { MissingReceiverHeaderException() }

private fun NativeMessageHeaderAccessor.getNativeHeader(
    key: String,
    onException: () -> MissingMandatoryHeaderException,
): String = getNativeHeader(key)?.first() ?: throw onException()
