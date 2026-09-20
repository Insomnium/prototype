package net.ins.prototype.backend.conf

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.kafka.autoconfigure.KafkaProperties

@ConfigurationProperties("app")
data class AppProperties(
    val kafka: KafkaProperties,
    val integrations: Integrations,
    val objectStorage: ObjectStorage,
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

data class ObjectStorage(
    val connectionUrl: String,
    val cdnBaseUrl: String,
    val user: String,
    val password: String,
    val photoBucket: String,
    val profilePhotoFolder: String,
) {

    override fun toString(): String {
        return "ObjectStorage(url='$connectionUrl', user='$user', photoBucket='$photoBucket', profilePhotoFolder='$profilePhotoFolder')"
    }
}
