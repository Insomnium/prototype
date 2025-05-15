package net.ins.prototype.backend.conf

import net.ins.prototype.backend.profile.event.ProfileEvent
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory

@Configuration
class KafkaConf(
    val appProperties: AppProperties,
) {

    companion object {
        const val PROFILE_EVENT_PRODUCER_FACTORY = "profileEventProducerFactory"
    }

    @Bean(PROFILE_EVENT_PRODUCER_FACTORY)
    fun profileEventProducerFactory(): ProducerFactory<Long, ProfileEvent> =
        DefaultKafkaProducerFactory(appProperties.kafka.buildProducerProperties())

    @Bean
    fun profileEventKafkaTemplate(
        @Qualifier(PROFILE_EVENT_PRODUCER_FACTORY) factory: ProducerFactory<Long, ProfileEvent>,
    ): KafkaTemplate<Long, ProfileEvent> = KafkaTemplate(factory)
}
