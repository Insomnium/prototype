package net.ins.prototype.chat.conf

import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient
import io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializer
import net.ins.prototype.backend.chat.event.MessageEvent
import org.apache.kafka.common.serialization.LongSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.ProducerFactory

@Configuration
class KafkaConf(
    private val appProperties: AppProperties,
) {

    @Bean
    fun chatMessageEventProducerFactory(
        schemaRegistryClient: SchemaRegistryClient,
    ): ProducerFactory<Long, MessageEvent> = DefaultKafkaProducerFactory(
        appProperties.kafka.buildProducerProperties(),
        LongSerializer(),
        KafkaProtobufSerializer<MessageEvent>(
            schemaRegistryClient,
            appProperties.kafka.buildProducerProperties()
        )
    )
}
