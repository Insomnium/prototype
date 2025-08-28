package net.ins.prototype.chat.conf

import com.google.protobuf.Message
import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient
import io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializer
import io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializer
import jakarta.annotation.PostConstruct
import net.ins.prototype.chat.event.UnserializableMessageWrapper.UnserializableMessage
import net.ins.prototype.common.logger
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer

@Configuration
class KafkaConf(
    private val appProperties: AppProperties,
) {

    companion object {
        const val P2P_CHAT_MESSAGE_EVENT_PRODUCER_FACTORY = "p2pChatMessageEventProducerFactory"
        const val P2P_CHAT_MESSAGE_EVENT_SERIALIZER = "p2pChatMessageEventSerializer"

        const val P2P_CHAT_MESSAGE_EVENT_CONSUMER_FACTORY = "p2pChatMessageEventConsumerFactory"
        const val P2P_CHAT_MESSAGE_EVENT_DESERIALIZER = "p2pChatMessageEventDeserializer"
        const val P2P_CHAT_MESSAGE_ERROR_HANDLING_DESERIALIZER = "p2pChatMessageEventErrorHandlingDeserializer"
    }

    @Bean
    fun schemaRegistryClient(): SchemaRegistryClient = CachedSchemaRegistryClient(
        appProperties.kafka.consumer.properties["schema.registry.url"],
        100,
    )

    // -------------------      Producer      -------------------

    @Bean(P2P_CHAT_MESSAGE_EVENT_SERIALIZER)
    fun prpMessageEventSerializer(schemaRegistryClient: SchemaRegistryClient): Serializer<Message> =
        KafkaProtobufSerializer(schemaRegistryClient)

    @Bean(P2P_CHAT_MESSAGE_EVENT_PRODUCER_FACTORY)
    fun p2pMessageEventProducerFactory(
        schemaRegistryClient: SchemaRegistryClient,
    ): ProducerFactory<String, Message> = DefaultKafkaProducerFactory(
        appProperties.kafka.buildProducerProperties(),
        StringSerializer(),
        KafkaProtobufSerializer(
            schemaRegistryClient,
            appProperties.kafka.buildProducerProperties()
        )
    )

    @Bean
    fun p2pMessageEventKafkaTemplate(
        @Qualifier(P2P_CHAT_MESSAGE_EVENT_PRODUCER_FACTORY) producerFactory: ProducerFactory<String, Message>,
    ): KafkaTemplate<String, Message> = KafkaTemplate(producerFactory)

    // -------------------      Consumer      -------------------

    @Bean(P2P_CHAT_MESSAGE_EVENT_DESERIALIZER)
    fun p2pMessageEventDeserializer(schemaRegistryClient: SchemaRegistryClient): Deserializer<Message> =
        KafkaProtobufDeserializer(schemaRegistryClient)

    @Bean(P2P_CHAT_MESSAGE_ERROR_HANDLING_DESERIALIZER)
    fun errorHandlingDeserializer(
        @Qualifier(P2P_CHAT_MESSAGE_EVENT_DESERIALIZER) deserializer: Deserializer<Message>,
    ): ErrorHandlingDeserializer<Message> = ErrorHandlingDeserializer(deserializer).apply {
        setFailedDeserializationFunction {
            logger.error("Failed to deserialize message from ${it.topic}", it.exception)
            UnserializableMessage.newBuilder().build()
        }
    }

    @Bean(P2P_CHAT_MESSAGE_EVENT_CONSUMER_FACTORY)
    fun p2pMessageEventConsumerFactory(
        @Qualifier(P2P_CHAT_MESSAGE_EVENT_DESERIALIZER) deserializer: Deserializer<Message>
    ): ConsumerFactory<String, Message> =
        DefaultKafkaConsumerFactory<String, Message>(appProperties.kafka.buildConsumerProperties()).apply {
            setValueDeserializer(deserializer)
        }
}
