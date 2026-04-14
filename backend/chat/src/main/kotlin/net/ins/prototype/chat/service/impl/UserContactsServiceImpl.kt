package net.ins.prototype.chat.service.impl

import net.ins.prototype.chat.dao.entity.ContactByUserCEntity
import net.ins.prototype.chat.dao.repo.UserContactsCRepo
import net.ins.prototype.chat.service.UserContactsService
import org.springframework.stereotype.Service

@Service
class UserContactsServiceImpl(
    private val repo: UserContactsCRepo
) : UserContactsService {

    override fun getUserContacts(userId: String): List<ContactByUserCEntity> =
        repo.findAllByUserId(userId)
}
