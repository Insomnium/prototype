package net.ins.prototype.chat.service.impl

import net.ins.prototype.chat.conf.AppProperties
import net.ins.prototype.chat.dao.entity.UserSessionCEntity
import net.ins.prototype.chat.dao.repo.UserSessionCRepo
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class UserSessionServiceImpl(
    private val appProperties: AppProperties,
    private val userSessionCRepo: UserSessionCRepo,
) {

    fun registerSession(userId: String) {
        userSessionCRepo.save(
            UserSessionCEntity(
                id = userId,
                instanceId = appProperties.instanceId,
                createdAt = Instant.now(),
            )
        )
    }
}
