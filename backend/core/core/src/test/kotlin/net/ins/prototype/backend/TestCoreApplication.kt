package net.ins.prototype.backend

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
    fromApplication<CoreApplication>().with(TestcontainersConfiguration::class).run(*args)
}
