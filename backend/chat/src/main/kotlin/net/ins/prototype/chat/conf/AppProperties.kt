package net.ins.prototype.chat.conf

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.kafka.autoconfigure.KafkaProperties

@ConfigurationProperties("app")
data class AppProperties(
    val kafka: KafkaProperties,
    val integrations: Integrations,
    val instanceId: String,
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
