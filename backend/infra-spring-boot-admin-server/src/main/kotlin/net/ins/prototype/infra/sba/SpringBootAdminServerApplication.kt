package net.ins.prototype.infra.sba

import de.codecentric.boot.admin.server.config.EnableAdminServer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableAdminServer
class SpringBootAdminServerApplication

fun main(args: Array<String>) {
    runApplication<SpringBootAdminServerApplication>(*args)
}