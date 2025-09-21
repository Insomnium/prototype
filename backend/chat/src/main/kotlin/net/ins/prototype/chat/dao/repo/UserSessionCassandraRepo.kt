package net.ins.prototype.chat.dao.repo

import net.ins.prototype.chat.dao.entity.UserSessionCassandraEntity
import org.springframework.data.repository.CrudRepository

interface UserSessionCassandraRepo : CrudRepository<UserSessionCassandraEntity, String>
