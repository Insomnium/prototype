package net.ins.prototype.chat.socket

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.equals.shouldBeEqual
import net.ins.prototype.chat.AbstractTestcontainersTest
import net.ins.prototype.chat.model.ChatMessageResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.simp.stomp.StompHeaders
import org.springframework.messaging.simp.stomp.StompSession
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.messaging.WebSocketStompClient
import org.springframework.web.socket.sockjs.client.SockJsClient
import org.springframework.web.socket.sockjs.client.WebSocketTransport
import java.lang.reflect.Type
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

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
        val receivedMessages = CopyOnWriteArrayList<ChatMessageResponse>()
        val countDownLatch = CountDownLatch(1)
        stompClient.connectAsync(
            "http://localhost:$serverPort/ws?userId=$USER_A",
            object : StompSessionHandlerAdapter() {

                override fun afterConnected(
                    session: StompSession,
                    connectedHeaders: StompHeaders
                ) {
                    session.subscribe("/user/topic/messages", object : StompSessionHandlerAdapter() {
                        override fun getPayloadType(headers: StompHeaders): Type = ChatMessageResponse::class.java

                        override fun handleFrame(
                            headers: StompHeaders,
                            payload: Any?
                        ) {
                            receivedMessages.add(payload as ChatMessageResponse)
                            countDownLatch.countDown()
                        }
                    })

                }
            },
            *arrayOf<String>()
        ).get(3, TimeUnit.SECONDS)

        countDownLatch.await(3, TimeUnit.SECONDS)

        receivedMessages shouldHaveSize 1
        receivedMessages[0].messages shouldHaveSize 1
        receivedMessages[0].messages[0].content shouldBeEqual "Welcome back online"
    }
}