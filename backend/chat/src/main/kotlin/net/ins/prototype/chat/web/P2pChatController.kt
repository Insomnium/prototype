package net.ins.prototype.chat.web

import net.ins.prototype.chat.model.ChatMessageRequest
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/p2p")
class P2pChatController(
    private val messageTemplate: SimpMessagingTemplate,
) {

    @PostMapping("/fanout")
    fun notifyAll(@RequestBody notification: ChatMessageRequest) {
        messageTemplate.convertAndSend("/topic/messages", notification)
    }
}
