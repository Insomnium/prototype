package net.ins.prototype.chat.socket

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.equals.shouldBeEqual
import net.ins.prototype.chat.AbstractTestcontainersTest
import net.ins.prototype.chat.PrototypeStompClientSessionProvider
import net.ins.prototype.chat.model.ChatMessageRequest
import net.ins.prototype.chat.socket.auth.P2pConstants
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@Import(AbstractTestcontainersTest::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class P2pChatHandlerTest : AbstractTestcontainersTest() {

    companion object {
        private const val USER_A = 1
        private const val USER_B = 2
        private const val USER_C = 3

        private const val MESSAGE_AWAIT_TIMEOUT_MILLIS = 5000L
    }

    @LocalServerPort
    private var serverPort: Int = 0

    private val sessionProvider: PrototypeStompClientSessionProvider = PrototypeStompClientSessionProvider()

    @AfterEach
    fun tearDown() {
        sessionProvider.terminate()
    }

    @Test
    fun `should establish session and receive admin hello`() {
        val sessionWrapperA = sessionProvider.establishSession(
            serverPort = serverPort,
            userId = USER_A,
        )

        val receivedMessagesA = sessionWrapperA.awaitForMessages(
            expectedMessagesCount = 1,
            userMessagesOnly = false,
            awaitMs = MESSAGE_AWAIT_TIMEOUT_MILLIS,
        )

        receivedMessagesA shouldHaveSize 1
        receivedMessagesA[0].sender shouldBeEqual P2pConstants.ADMIN_SENDER_ID
        receivedMessagesA[0].content shouldBeEqual "Welcome back online"
    }

    @Test
    fun `should exchange messages`() {
        // given 3 user sessions established:
        val sessionWrapperA = sessionProvider.establishSession(serverPort, userId = USER_A)
        val sessionWrapperB = sessionProvider.establishSession(serverPort, userId = USER_B)
        val sessionWrapperC = sessionProvider.establishSession(serverPort, userId = USER_C)

        // when: USER_A sends message to USER_B
        val messageContent = "Hey, UserB"
        sessionWrapperA.sendMessage(receiverId = USER_B, payload = ChatMessageRequest(messageContent))

        // then: USER_B receives message
        val receivedMessagesB = sessionWrapperB.awaitForMessages(expectedMessagesCount = 1, awaitMs = MESSAGE_AWAIT_TIMEOUT_MILLIS)
        receivedMessagesB shouldHaveSize 1
        receivedMessagesB[0].sender shouldBeEqual USER_A.toString()
        receivedMessagesB[0].content shouldBeEqual messageContent

        // and: USER_A does not receive any messages
        val receivedMessagesA = sessionWrapperA.awaitForMessages(expectedMessagesCount = Int.MAX_VALUE, awaitMs = MESSAGE_AWAIT_TIMEOUT_MILLIS)
        receivedMessagesA.shouldBeEmpty()

        // and: USER_C does not receive any messages
        val receivedMessagesC = sessionWrapperC.awaitForMessages(expectedMessagesCount = Int.MAX_VALUE, awaitMs = MESSAGE_AWAIT_TIMEOUT_MILLIS)
        receivedMessagesC.shouldBeEmpty()
    }
}
