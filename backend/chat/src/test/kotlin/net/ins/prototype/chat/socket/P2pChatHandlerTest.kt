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
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class P2pChatHandlerTest : AbstractTestcontainersTest() {

    @LocalServerPort
    private var serverPort: Int = 0

    private val sessionProvider: PrototypeStompClientSessionProvider = PrototypeStompClientSessionProvider()

    private val USER_A = 1
    private val USER_B = 2
    private val USER_C = 3

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

        val sessionWrapperB = sessionProvider.establishSession(
            serverPort = serverPort,
            userId = USER_B,
        )

        val receivedMessagesB = sessionWrapperB.awaitForMessages(
            expectedMessagesCount = 1,
            userMessagesOnly = false,
        )

        receivedMessagesB shouldHaveSize 1
        receivedMessagesB[0].sender shouldBeEqual P2pConstants.ADMIN_SENDER_ID
        receivedMessagesB[0].content shouldBeEqual "Welcome back online"
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

        // then: USER_B receives messaeg
        val receivedMessagesB = sessionWrapperB.awaitForMessages(expectedMessagesCount = 1)
        receivedMessagesB shouldHaveSize 1
        receivedMessagesB[0].sender shouldBeEqual USER_A.toString()
        receivedMessagesB[0].content shouldBeEqual messageContent

        // and: USER_A does not receive any messages
        val receivedMessagesA = sessionWrapperA.awaitForMessages(expectedMessagesCount = Int.MAX_VALUE)
        receivedMessagesA.shouldBeEmpty()

        // and: USER_C does not receive any messages
        val receivedMessagesC = sessionWrapperC.awaitForMessages(expectedMessagesCount = Int.MAX_VALUE)
        receivedMessagesC.shouldBeEmpty()
    }
}