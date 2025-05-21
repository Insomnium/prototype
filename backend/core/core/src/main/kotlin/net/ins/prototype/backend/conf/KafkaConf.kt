package net.ins.prototype.backend.conf

import com.github.thake.kafka.avro4k.serializer.KafkaAvro4kSerializer
import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient
import net.ins.prototype.backend.common.logger
import net.ins.prototype.backend.profile.event.Avro4kKotlinProfileDeserializer
import net.ins.prototype.backend.profile.event.ProfileEvent
import net.ins.prototype.backend.profile.event.UnserializableProfileEvent
import org.apache.kafka.common.serialization.LongSerializer
import org.apache.kafka.common.serialization.Serializer
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer

@Configuration
class KafkaConf(
    val appProperties: AppProperties,
) {

    companion object {
        const val PROFILE_EVENT_PRODUCER_FACTORY = "profileEventProducerFactory"

        const val PROFILE_EVENT_CONSUMER_FACTORY = "profileEventConsumerFactory"
        const val PROFILE_EVENT_LISTENER_CONTAINER_FACTORY = "profileEventListenerContainerFactory"
        const val PROFILE_ERROR_HANDLING_DESERIALIZER = "profileErrorHandlingDeserializer"
    }

    @Bean(PROFILE_EVENT_PRODUCER_FACTORY)
    fun profileEventProducerFactory(
        schemaRegistryClient: SchemaRegistryClient,
    ): ProducerFactory<Long, ProfileEvent> =
        DefaultKafkaProducerFactory(
            appProperties.kafka.buildProducerProperties(),
            LongSerializer(),
            KafkaAvro4kSerializer(
                client = schemaRegistryClient,
                props = appProperties.kafka.buildProducerProperties(),
            ) as Serializer<ProfileEvent>,
        )

    @Bean
    fun profileEventKafkaTemplate(
        @Qualifier(PROFILE_EVENT_PRODUCER_FACTORY) factory: ProducerFactory<Long, ProfileEvent>,
    ): KafkaTemplate<Long, ProfileEvent> = KafkaTemplate(factory)

    @Bean
    fun schemaRegistryClient(): SchemaRegistryClient = CachedSchemaRegistryClient(
        appProperties.kafka.consumer.properties["schema.registry.url"],
        100
    )

    @Bean(PROFILE_ERROR_HANDLING_DESERIALIZER)
    @Suppress("UNCHECKED_CAST")
    fun errorHandlingDeserializer(
        schemaRegistryClient: SchemaRegistryClient
    ): ErrorHandlingDeserializer<ProfileEvent> = ErrorHandlingDeserializer(
        Avro4kKotlinProfileDeserializer<ProfileEvent>(schemaRegistryClient)
    ).apply {
        setFailedDeserializationFunction {
            logger.error("Failed to deserialize message from ${it.topic}", it.exception)
            UnserializableProfileEvent()
        }
    }

    @Bean(PROFILE_EVENT_CONSUMER_FACTORY)
    fun profileEventConsumerFactory(
        @Qualifier(PROFILE_ERROR_HANDLING_DESERIALIZER) errorHandlingDeserializer: ErrorHandlingDeserializer<ProfileEvent>,
    ): ConsumerFactory<Long, ProfileEvent> =
        DefaultKafkaConsumerFactory<Long, ProfileEvent>(appProperties.kafka.buildConsumerProperties()).apply {
            setValueDeserializer(errorHandlingDeserializer)
        }

    @Bean(PROFILE_EVENT_LISTENER_CONTAINER_FACTORY)
    fun profileEventListenerContainerFactory(
        @Qualifier(PROFILE_EVENT_CONSUMER_FACTORY) profileEventConsumerFactory: ConsumerFactory<Long, ProfileEvent>
    ): ConcurrentKafkaListenerContainerFactory<Long, ProfileEvent> = ConcurrentKafkaListenerContainerFactory<Long, ProfileEvent>().apply {
        consumerFactory = profileEventConsumerFactory
        isBatchListener = false
        containerProperties.ackMode = ContainerProperties.AckMode.MANUAL_IMMEDIATE
    }
}
