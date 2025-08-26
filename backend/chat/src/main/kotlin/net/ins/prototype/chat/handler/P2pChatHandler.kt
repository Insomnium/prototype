package net.ins.prototype.chat.handler

import net.ins.prototype.chat.ChatMessage
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Controller

@Controller
class P2pChatHandler {

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    fun onMessageReceived(message: ChatMessage): ChatMessage = ChatMessage(
        content = "Hey, I've just received your message: ${message.content}",
    )
}
