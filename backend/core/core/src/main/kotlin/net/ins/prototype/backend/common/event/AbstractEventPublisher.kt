package net.ins.prototype.backend.common.event

import net.ins.prototype.backend.common.logger
import net.ins.prototype.backend.conf.AppProperties
import net.ins.prototype.backend.conf.Topics
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate

abstract class AbstractEventPublisher<S : Any, K : Any, V : Any> {

    @Autowired
    private lateinit var kafkaTemplate: KafkaTemplate<K, V>

    @Autowired
    private lateinit var appProperties: AppProperties

    protected abstract fun payload(source: S): V

    protected abstract fun key(source: S): K

    protected abstract fun topic(topics: Topics): String

    fun publish(source: S) {
        val key = key(source)
        val payload = payload(source)
        logger.trace("Publishing event [key={}; payload={}]", key, payload)
        kafkaTemplate.send(
            ProducerRecord(
                topic(appProperties.integrations.topics),
                key,
                payload,
            )
        )
    }
}
