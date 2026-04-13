package net.ins.prototype.chat

import net.ins.prototype.chat.model.ChatMessage
import net.ins.prototype.chat.model.ChatMessageResponse
import org.springframework.messaging.simp.stomp.StompHeaders
import org.springframework.messaging.simp.stomp.StompSession
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter
import org.springframework.web.socket.messaging.WebSocketStompClient
import java.lang.reflect.Type
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

fun WebSocketStompClient.establishSession(
    serverPort: Int,
    userId: Int,
    awaitMs: Long = 1000,
): StompSession = connectAsync(
    "http://localhost:$serverPort/ws?userId=$userId",
    object : StompSessionHandlerAdapter() {},
    *arrayOf<String>()
).get(awaitMs, TimeUnit.MILLISECONDS)

fun StompSession.awaitForMessages(
    topic: String = "/user/topic/messages",
    expectedMessagesCount: Int = 1,
    userMessagesOnly: Boolean = true,
): CopyOnWriteArrayList<ChatMessage> {
    val receivedMessages = CopyOnWriteArrayList<ChatMessage>()
    val countDownLatch = CountDownLatch(expectedMessagesCount)
    subscribe(topic, object : StompSessionHandlerAdapter() {

        override fun getPayloadType(headers: StompHeaders): Type = ChatMessageResponse::class.java

        override fun handleFrame(
            headers: StompHeaders,
            payload: Any?
        ) {
            val message = payload as ChatMessageResponse
            receivedMessages.addAll(if (userMessagesOnly) message.userMessages else message.messages)
            repeat(receivedMessages.size) { countDownLatch.countDown() }
        }
    })
    countDownLatch.await(3, TimeUnit.SECONDS)
    return receivedMessages
}