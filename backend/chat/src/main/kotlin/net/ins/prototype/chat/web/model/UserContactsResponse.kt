package net.ins.prototype.chat.web.model

data class UserContactsResponse(
    val contacts: List<UserContact>,
)

data class UserContact(
    val userId: String,
    val contactId: String,
)
