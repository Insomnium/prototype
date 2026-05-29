package net.ins.prototype.chat.model

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import net.ins.prototype.chat.InvalidParticipantIdException
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class ChatRoomIdTest {

    @ParameterizedTest
    @MethodSource("validIds")
    fun `should build value chat room id`(
        firstParticipant: String,
        secondParticipant: String,
        expectedChatRoomId: String,
    ) {
        ChatRoomId(firstParticipant, secondParticipant).toString() shouldBe expectedChatRoomId
        ChatRoomId(firstParticipant, secondParticipant).toRoomId() shouldBe expectedChatRoomId
    }

    @ParameterizedTest
    @MethodSource("invalidIds")
    fun `should fail on invalid room id`(
        firstParticipant: String,
        secondParticipant: String,
        invalidId: String,
    ) {
        assertThrows<InvalidParticipantIdException> {
            ChatRoomId(firstParticipant, secondParticipant).toRoomId()
        }.apply {
            message shouldContain invalidId
        }

        assertThrows<InvalidParticipantIdException> {
            ChatRoomId(firstParticipant, secondParticipant).toString()
        }.apply {
            message shouldContain invalidId
        }
    }

    companion object {

        @JvmStatic
        fun validIds(): Stream<Arguments> = Stream.of(
            Arguments.of("0", "1", "0_1"),
            Arguments.of("123", "0", "0_123"),
            Arguments.of("20", "15", "15_20"),
            Arguments.of("10", "15", "10_15"),
            Arguments.of("15", "10", "10_15"),
            Arguments.of("3", "20", "3_20"),
            Arguments.of("20", "3", "3_20"),
            Arguments.of("1", "1", "1_1"),
        )

        @JvmStatic
        fun invalidIds(): Stream<Arguments> = Stream.of(
            Arguments.of("0", "a", "a"),
            Arguments.of("asd", "0", "asd"),
            Arguments.of("f", "0", "f"),
        )
    }
}
