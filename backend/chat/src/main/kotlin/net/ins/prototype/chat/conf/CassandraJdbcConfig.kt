package net.ins.prototype.chat.conf

import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class CassandraJdbcConfig {
//
//    @Autowired
//    private lateinit var dataSource: DataSource

//    @Autowired(required = false)
//    private lateinit var connectionCAllback: ConnectionCallback?

    @PostConstruct
    fun debug() {
        val debug = "debug"
    }
}
