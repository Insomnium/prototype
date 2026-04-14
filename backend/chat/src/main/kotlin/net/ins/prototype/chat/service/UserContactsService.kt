package net.ins.prototype.chat.service

import net.ins.prototype.chat.dao.entity.ContactByUserCEntity

interface UserContactsService {

    fun getUserContacts(userId: String): List<ContactByUserCEntity>
}
