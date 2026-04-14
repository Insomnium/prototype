package net.ins.prototype.chat

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import net.ins.prototype.chat.model.ChatMessage
import net.ins.prototype.chat.model.ChatMessageRequest
import net.ins.prototype.chat.socket.auth.P2pWsHeaders
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.simp.stomp.StompHeaders
import org.springframework.messaging.simp.stomp.StompSession
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.messaging.WebSocketStompClient
import org.springframework.web.socket.sockjs.client.SockJsClient
import org.springframework.web.socket.sockjs.client.WebSocketTransport

class PrototypeStompClientSessionProvider(
) {

    private val sessionByUser: MutableMap<Int, StompSessionWrapper> = mutableMapOf()

    companion object {
        private val jsonMessageConverter: MappingJackson2MessageConverter = MappingJackson2MessageConverter().apply {
            objectMapper = ObjectMapper().registerKotlinModule()
        }
    }

    fun establishSession(
        serverPort: Int,
        userId: Int,
        awaitMs: Long = 1000,
    ): StompSessionWrapper = sessionByUser.compute(userId) { key, existing ->
        existing?.let { throw IllegalStateException("User $userId has already associated session") }
        val client = buildClient()
        val session = client.establishSession(
            serverPort = serverPort,
            userId = userId,
            awaitMs = awaitMs,
        )
        StompSessionWrapper(client, session, key)
    }!!

    fun terminate() {
        sessionByUser.values.forEach { sessionWrapper -> sessionWrapper.terminate() }
        sessionByUser.clear()
    }

    private fun buildClient(): WebSocketStompClient =
        WebSocketStompClient(SockJsClient(listOf(WebSocketTransport(StandardWebSocketClient())))).apply {
            messageConverter = jsonMessageConverter
        }

    data class StompSessionWrapper(
        val stompClient: WebSocketStompClient,
        val session: StompSession,
        private val userId: Int,
    ) {
        fun awaitForMessages(
            topic: String = "/user/topic/messages",
            expectedMessagesCount: Int = 1,
            userMessagesOnly: Boolean = true,
            awaitMs: Long = 1000,
        ): List<ChatMessage> = session.awaitForMessages(
            topic = topic,
            expectedMessagesCount = expectedMessagesCount,
            userMessagesOnly = userMessagesOnly,
            awaitMs = awaitMs,
        )

        fun sendMessage(
            receiverId: Int,
            payload: ChatMessageRequest,
        ) {
            val headers = StompHeaders().apply {
                destination = "/app/chat/${buildChatRoomId(receiverId)}"
                set(P2pWsHeaders.SENDER, userId.toString())
                set(P2pWsHeaders.RECEIVER, receiverId.toString())
            }
            session.send(headers, payload)
        }

        fun terminate() {
            session.disconnect()
            stompClient.stop()
        }


        private fun buildChatRoomId(receiverId: Int): String =
            with(sortedSetOf(userId, receiverId)) { return "${first - last}" }
    }
}