package net.ins.prototype.backend

import net.ins.prototype.backend.conf.AppProperties
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.LongDeserializer
import org.awaitility.kotlin.await
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

@Suppress("UNCHECKED_CAST")
object KafkaTestConsumer {

    private val deserializersByTopic: MutableMap<String, KafkaConsumer<*, *>> = ConcurrentHashMap()

    fun <K : Any, V : Any> assertEventReceived(
        appProperties: AppProperties,
        topic: String,
        awaitDuration: Duration = Duration.ofSeconds(5),
        expectedRecordsCount: Int = 1,
        keyDeserializer: Deserializer<K> = LongDeserializer() as Deserializer<K>,
        valueDeserializer: Deserializer<V>,
        asserttion: (List<ConsumerRecord<K, V>>) -> Unit,
    ) {
        val consumer = deserializersByTopic.computeIfAbsent(topic) {
            KafkaConsumer<K, V>(
                mapOf(
                    ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to appProperties.kafka.bootstrapServers,
                    ConsumerConfig.GROUP_ID_CONFIG to "test-group",
                    ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest"
                ),
                keyDeserializer,
                valueDeserializer,
            ) as KafkaConsumer<K, V>
        }.apply {
            unsubscribe()
            subscribe(listOf(topic))
        }

        var records: MutableList<ConsumerRecord<K, V>> = mutableListOf()
        await.atMost(awaitDuration).until {
            val recs  = consumer.poll(Duration.ofSeconds(3))
            records += recs as List<ConsumerRecord<K, V>>
            records.size >= expectedRecordsCount
        }

        asserttion(records)
    }
}
