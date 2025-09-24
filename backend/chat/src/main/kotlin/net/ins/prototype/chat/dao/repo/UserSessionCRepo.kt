package net.ins.prototype.chat.dao.repo

import net.ins.prototype.chat.dao.entity.UserSessionCEntity
import org.springframework.data.repository.CrudRepository

interface UserSessionCassandraRepo : CrudRepository<UserSessionCEntity, String>
