package net.ins.prototype.chat.service

import net.ins.prototype.chat.conf.AppProperties
import net.ins.prototype.chat.dao.entity.UserSessionCassandraEntity
import net.ins.prototype.chat.dao.repo.UserSessionCassandraRepo
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class UserSessionService(
    private val appProperties: AppProperties,
    private val userSessionCassandraRepo: UserSessionCassandraRepo,
) {

    fun registerSession(userId: String) {
        userSessionCassandraRepo.save(
            UserSessionCassandraEntity(
                id = userId,
                instanceId = appProperties.instanceId,
                createdAt = Instant.now(),
            )
        )
    }
}
