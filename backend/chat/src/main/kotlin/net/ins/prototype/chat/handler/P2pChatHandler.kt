package net.ins.prototype.chat.handler

import net.ins.prototype.chat.auth.receiverId
import net.ins.prototype.chat.auth.senderId
import net.ins.prototype.chat.model.ChatMessageRequest
import net.ins.prototype.chat.model.ChatMessageResponse
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
    fun onMessageReceived(
        @Payload message: ChatMessageRequest,
        headerAccessor: SimpMessageHeaderAccessor,
    ) {
        messagingTemplate.convertAndSendToUser(
            headerAccessor.receiverId,
            "/topic/messages",
            ChatMessageResponse(
                sender = headerAccessor.senderId,
                content = message.content
            ),
        )
    }
}
