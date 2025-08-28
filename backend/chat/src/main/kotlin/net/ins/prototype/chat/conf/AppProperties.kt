package net.ins.prototype.chat.conf

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
    val p2pMessage: Topic,
)

data class Topic(
    val name: String,
)
