package net.ins.prototype.chat.socket

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.equals.shouldBeEqual
import net.ins.prototype.chat.AbstractTestcontainersTest
import net.ins.prototype.chat.awaitForMessages
import net.ins.prototype.chat.establishSession
import net.ins.prototype.chat.socket.auth.P2pConstants
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.messaging.WebSocketStompClient
import org.springframework.web.socket.sockjs.client.SockJsClient
import org.springframework.web.socket.sockjs.client.WebSocketTransport

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class P2pChatHandlerTest : AbstractTestcontainersTest() {

    @LocalServerPort
    private var serverPort: Int = 0

    private lateinit var stompClient: WebSocketStompClient

    private val USER_A = 1
    private val USER_B = 2
    private val USER_C = 3

    @BeforeEach
    fun setUp() {
        stompClient = WebSocketStompClient(SockJsClient(listOf(WebSocketTransport(StandardWebSocketClient()))))
        stompClient.messageConverter = MappingJackson2MessageConverter().apply {
            objectMapper = ObjectMapper().registerKotlinModule()
        }
    }

    @Test
    fun `should establish session and receive admin hello`() {
        val session = stompClient.establishSession(
            serverPort = serverPort,
            userId = USER_A,
        )

        val receivedMessages = session.awaitForMessages(
            expectedMessagesCount = 1,
            userMessagesOnly = false,
        )

        receivedMessages shouldHaveSize 1
        receivedMessages[0].sender shouldBeEqual P2pConstants.ADMIN_SENDER_ID
        receivedMessages[0].content shouldBeEqual "Welcome back online"
    }
}