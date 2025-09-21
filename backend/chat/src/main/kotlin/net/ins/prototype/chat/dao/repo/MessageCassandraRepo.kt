package net.ins.prototype.chat.dao.repo

import net.ins.prototype.chat.dao.entity.MessageCassandraEntity
import net.ins.prototype.chat.dao.entity.P2pMessagePk
import org.springframework.data.cassandra.repository.CassandraRepository

interface MessageCassandraRepo : CassandraRepository<MessageCassandraEntity, P2pMessagePk>
