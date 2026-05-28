package net.ins.prototype.chat.model

import net.ins.prototype.chat.InvalidParticipantIdException

data class ChatRoomId(val firstParticipant: String, val secondParticipant: String) {

    fun toRoomId(): String = listOf(firstParticipant.longValue(), secondParticipant.longValue()).sorted().joinToString("_")

    override fun toString(): String = toRoomId()

    private fun String.longValue(): Long = try {
        toLong()
    } catch (e: NumberFormatException) {
        throw InvalidParticipantIdException(this)
    }
}
