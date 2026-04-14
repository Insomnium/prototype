package net.ins.prototype.chat.web

import net.ins.prototype.chat.model.ChatMessageRequest
import net.ins.prototype.chat.service.UserContactsService
import net.ins.prototype.chat.web.model.UserContact
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/p2p")
class P2pChatController(
    private val messageTemplate: SimpMessagingTemplate,
    private val contactsService: UserContactsService,
) {

    @PostMapping("/fanout")
    fun notifyAll(@RequestBody notification: ChatMessageRequest) {
        messageTemplate.convertAndSend("/topic/messages", notification)
    }

    @GetMapping("/contacts")
    fun getUserContacts(
        @RequestHeader("X-User-Id") userId: String,
    ): List<UserContact> = contactsService.getUserContacts(userId).map {
        UserContact(it.userId, it.contactId)
    }
}
