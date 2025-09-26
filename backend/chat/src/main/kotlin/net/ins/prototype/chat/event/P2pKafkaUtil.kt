package net.ins.prototype.chat.event

import org.apache.kafka.common.header.internals.RecordHeader
import java.util.TreeSet

object P2pKafkaHeaders {
    const val SENDER = "senderId"
    const val RECEIVER = "receiverId"
    const val CHAT_ROOM = "chatRoomId"
}

fun P2pMessageContext.buildSenderHeader(): RecordHeader =
    RecordHeader(P2pKafkaHeaders.SENDER, senderId.toUtf8ByteArray())

fun P2pMessageContext.buildReceiverHeader(): RecordHeader =
    RecordHeader(P2pKafkaHeaders.RECEIVER, receiverId.toUtf8ByteArray())

fun P2pMessageContext.buildChatRoomHeader(): RecordHeader =
    RecordHeader(P2pKafkaHeaders.CHAT_ROOM, sortedSetOf(senderId, receiverId).buildChatRoomId().toUtf8ByteArray())

private fun String.toUtf8ByteArray(): ByteArray = toByteArray(charset = Charsets.UTF_8)

fun TreeSet<String>.buildChatRoomId(): String = "p2p_${first}_${last}"
