package net.ins.prototype.backend.conf

import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("app")
data class AppProperties(
    val kafka: KafkaProperties,
    val integrations: Integrations,
)

data class Integrations(
    val topics: Topics,
)

data class Topics(
    val profiles: Topic,
)

data class Topic(
    val name: String,
)
