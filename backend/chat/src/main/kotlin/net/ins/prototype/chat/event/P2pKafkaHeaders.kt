package net.ins.prototype.chat.event

import org.apache.kafka.common.header.internals.RecordHeader

object P2pKafkaHeaders {
    const val SENDER = "senderId"
    const val RECEIVER = "receiverId"
}

fun P2pMessageContext.buildSenderHeader(): RecordHeader =
    RecordHeader(P2pKafkaHeaders.SENDER, senderId.toUtf8ByteArray())

fun P2pMessageContext.buildReceiverHeader(): RecordHeader =
    RecordHeader(P2pKafkaHeaders.RECEIVER, receiverId.toUtf8ByteArray())

private fun String.toUtf8ByteArray(): ByteArray = toByteArray(charset = Charsets.UTF_8)
