package net.ins.prototype.chat.handler

import net.ins.prototype.chat.auth.receiverId
import net.ins.prototype.chat.model.ChatMessage
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller

@Controller
class P2pChatHandler(
    private val messagingTemplate: SimpMessagingTemplate,
) {

    @MessageMapping("/chat")
//    @SendTo("/topic/messages")
    fun onMessageReceived(
        @Payload message: ChatMessage,
        headerAccessor: SimpMessageHeaderAccessor,
    ) {
        messagingTemplate.convertAndSendToUser(
            headerAccessor.receiverId,
            "/topic/messages",
            ChatMessage(
                content = "Hey, I've just received your message: ${message.content}",
            ),
        )
    }
}
