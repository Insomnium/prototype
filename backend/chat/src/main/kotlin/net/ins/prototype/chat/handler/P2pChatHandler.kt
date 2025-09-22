package net.ins.prototype.chat.handler

import com.google.protobuf.Message
import jakarta.websocket.SendResult
import net.ins.prototype.chat.auth.receiverId
import net.ins.prototype.chat.auth.senderId
import net.ins.prototype.chat.conf.AppProperties
import net.ins.prototype.chat.event.P2pMessageContext
import net.ins.prototype.chat.event.P2pMessageEvent
import net.ins.prototype.chat.event.buildReceiverHeader
import net.ins.prototype.chat.event.buildSenderHeader
import net.ins.prototype.chat.model.ChatMessage
import net.ins.prototype.chat.model.ChatMessageRequest
import net.ins.prototype.chat.model.ChatMessageResponse
import net.ins.prototype.chat.service.impl.ChatIdGenerator
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.context.ApplicationListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller
import org.springframework.web.socket.messaging.SessionSubscribeEvent
import java.util.concurrent.CompletableFuture

@Controller
class P2pChatHandler(
    private val messagingTemplate: SimpMessagingTemplate,
    private val kafkaTemplate: KafkaTemplate<String, Message>,
    private val chatIdGenerator: ChatIdGenerator,
    appProperties: AppProperties,
) : ApplicationListener<SessionSubscribeEvent> {

    val p2pMessageTopic = appProperties.integrations.topics.p2pMessage

    @MessageMapping("/chat")
    fun onMessageReceived(
        @Payload message: ChatMessageRequest,
        headerAccessor: SimpMessageHeaderAccessor,
    ) {
        sendToKafka(buildContext(headerAccessor, message))
            .thenAccept { onSentContext -> sendToSocket(onSentContext) }
    }

    private fun sendToKafka(context: P2pMessageContext): CompletableFuture<P2pMessageContext> {
        val cf: CompletableFuture<P2pMessageContext> = CompletableFuture()
        kafkaTemplate.send(prepareRecord(context))
            .handleAsync { result, exception ->
                if (exception != null) {
                    cf.completeExceptionally(exception)
                } else {
                    cf.completeAsync { context }
                }
            }
        return cf
    }

    private fun sendToSocket(context: P2pMessageContext) {
        messagingTemplate.convertAndSendToUser(
            context.receiverId,
            "/topic/messages", // TODO: extract constants or app properties
            ChatMessageResponse(
                listOf(
                    ChatMessage(
                        sender = context.senderId,
                        content = context.content,
                    )
                )
            ),
        )
    }

    private fun prepareRecord(context: P2pMessageContext): ProducerRecord<String, Message> = ProducerRecord(
        p2pMessageTopic.name,
        null,
        null,
        chatIdGenerator.generateId(context),
        P2pMessageEvent.newBuilder()
            .setSenderId(context.senderId)
            .setReceiverId(context.receiverId)
            .setContent(context.content)
            .build() as Message,
        listOf(
            context.buildSenderHeader(),
            context.buildReceiverHeader(),
        ),
    )

    private fun buildContext(
        headerAccessor: SimpMessageHeaderAccessor,
        message: ChatMessageRequest
    ) = P2pMessageContext(
        senderId = headerAccessor.senderId,
        receiverId = headerAccessor.receiverId,
        content = message.content,
    )

    override fun onApplicationEvent(event: SessionSubscribeEvent) {
        val subscribedUserId = requireNotNull(event.user?.name)
        sendToSocket(P2pMessageContext("admin", subscribedUserId, "Welcome back online"))
    }
}
