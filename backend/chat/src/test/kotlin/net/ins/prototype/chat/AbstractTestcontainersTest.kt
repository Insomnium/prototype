package net.ins.prototype.chat

import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient
import net.ins.prototype.chat.conf.AppProperties
import net.ins.prototype.chat.socket.auth.P2pConstants
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.awaitility.kotlin.await
import org.junit.jupiter.api.BeforeAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.cassandra.CassandraContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.kafka.ConfluentKafkaContainer
import org.testcontainers.redpanda.RedpandaContainer
import org.testcontainers.utility.DockerImageName
import org.testcontainers.utility.MountableFile
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

@Testcontainers
open class AbstractTestcontainersTest {

    @Autowired
    protected lateinit var appProperties: AppProperties

    @Autowired
    protected lateinit var schemaRegistryClient: SchemaRegistryClient

    private val deserializersByTopic: MutableMap<String, KafkaConsumer<*, *>> = ConcurrentHashMap()

    companion object {

        const val CASSANDRA_DATACENTER = "prototype-dc1"
        const val SCHEMA_REGISTRY_P2P_MESSAGE_EVENT_SCHEMA_FILE = "/tmp/p2p_message_event.json"
        const val SCHEMA_REGISTRY_PORT = 8081

        @JvmStatic
        @Container
        @ServiceConnection
        private val kafkaContainer = ConfluentKafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.8.0"))

        @JvmStatic
        @Container
        @ServiceConnection
        val cassandraContainer = CassandraContainer("cassandra:5.0.5")
            .withEnv("CASSANDRA_DC", "prototype-dc1")
            .withInitScript("cassandra/init-chat.cql")

        @JvmStatic
        @Container
        private val redpandaContainer: RedpandaContainer =
            RedpandaContainer(DockerImageName.parse("docker.redpanda.com/redpandadata/redpanda:v23.1.2"))
                .withCopyFileToContainer(
                    MountableFile.forClasspathResource("kafka/schema/p2p_message_event_registraion.json"),
                    SCHEMA_REGISTRY_P2P_MESSAGE_EVENT_SCHEMA_FILE,
                )

        @DynamicPropertySource
        @JvmStatic
        fun dynamicProperties(registry: DynamicPropertyRegistry) {
            registry.add("app.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers)
            registry.add("app.kafka.producer.properties.schema.registry.url") { redpandaContainer.schemaRegistryAddress }
            registry.add("app.kafka.consumer.properties.schema.registry.url") { redpandaContainer.schemaRegistryAddress }
            registry.add("spring.cassandra.contact-points") { cassandraContainer.contactPoints }
            registry.add("spring.cassandra.local-datacenter") { CASSANDRA_DATACENTER }
            registry.add("spring.liquibase.url") { "jdbc:cassandra://${cassandraContainer.contactPoints}/chat?compliancemode=Liquibase&localdatacenter=$CASSANDRA_DATACENTER" }
        }

        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            redpandaContainer.execInContainer(
                "/bin/sh",
                "-c",
                "curl -XPOST -H 'Content-Type: application/json' --data-binary @$SCHEMA_REGISTRY_P2P_MESSAGE_EVENT_SCHEMA_FILE http://localhost:$SCHEMA_REGISTRY_PORT/subjects/${P2pConstants.P2P_MESSAGE_EVENT_SUBJECT}/versions"
            )
        }

        private val CassandraContainer.contactPoints: String
            get() = "$host:$firstMappedPort"
    }

    @Suppress("UNCHECKED_CAST")
    protected fun <K : Any, V : Any> assertEventPublished(
        topic: String,
        awaitDuration: Duration = Duration.ofSeconds(5),
        expectedRecordsCount: Int = 1,
        keyDeserializer: Deserializer<K> = StringDeserializer() as Deserializer<K>,
        valueDeserializer: Deserializer<V>,
        assertion: (List<ConsumerRecord<K, V>>) -> Unit,
    ) {
        val consumer = deserializersByTopic.computeIfAbsent(topic) {
            val kafkaConsumer = KafkaConsumer<K, V>(
                mapOf(
                    ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to appProperties.kafka.bootstrapServers,
                    ConsumerConfig.GROUP_ID_CONFIG to "test-group",
                    ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",
                ),
                keyDeserializer,
                valueDeserializer,
            ) as KafkaConsumer<K, V>

            kafkaConsumer.apply { subscribe(listOf(topic)) }
        }

        val records: MutableList<ConsumerRecord<K, V>> = mutableListOf()
        await.atMost(awaitDuration).until {
            val recs = consumer.poll(Duration.ofSeconds(3))
            records += recs.records(topic) as Iterable<ConsumerRecord<K, V>>
            records.size >= expectedRecordsCount
        }

        assertion(records)
    }
}