package net.ins.prototype.backend.conf

import net.ins.prototype.backend.profile.event.ProfileCreatedEvent
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.LongDeserializer
import org.awaitility.kotlin.await
import org.junit.jupiter.api.BeforeAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.elasticsearch.ElasticsearchContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.kafka.ConfluentKafkaContainer
import org.testcontainers.redpanda.RedpandaContainer
import org.testcontainers.utility.DockerImageName
import org.testcontainers.utility.MountableFile
import java.io.InputStream
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

@Testcontainers
open class AbstractTestcontainersTest {

    @Autowired
    protected lateinit var appProperties: AppProperties

    @MockitoSpyBean
    @Autowired
    protected lateinit var esOperations: ElasticsearchOperations

    companion object {

        const val DEFAULT_PROFILES_ES_CONTAINER_FILE_PATH = "/tmp/profiles.ndjson"
        const val DEFAULT_PROFILES_ES_INDEX = "profile"

        const val SCHEMA_REGISTRY_PROFILE_SCHEMA_FILE = "/tmp/profile.json"
        const val SCHEMA_REGISTRY_PORT = 8081

        private val deserializersByTopic: MutableMap<String, KafkaConsumer<*, *>> = ConcurrentHashMap()

        @JvmStatic
        @Container
        @ServiceConnection
        val postgresContainer: PostgreSQLContainer<*> = PostgreSQLContainer(DockerImageName.parse("postgres:17.4-alpine3.21"))
            .withUsername("test")
            .withPassword("test")
            .withDatabaseName("test")

        @JvmStatic
        @Container
        @ServiceConnection
        val elasticSearchContainer: ElasticsearchContainer = ElasticsearchContainer(DockerImageName.parse("elasticsearch:8.17.3"))
            .withEnv("node.name", "elasticsearch")
            .withEnv("xpack.security.enabled", "false")
            .withCopyFileToContainer(MountableFile.forClasspathResource("es/profiles.ndjson"), DEFAULT_PROFILES_ES_CONTAINER_FILE_PATH)

        @JvmStatic
        @Container
        private val kafkaContainer = ConfluentKafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.8.0"))

        @JvmStatic
        @Container
        private val redpandaContainer: RedpandaContainer =
            RedpandaContainer(DockerImageName.parse("docker.redpanda.com/redpandadata/redpanda:v23.1.2"))
                .withCopyFileToContainer(
                    MountableFile.forClasspathResource("kafka/schema/profile.json"),
                    SCHEMA_REGISTRY_PROFILE_SCHEMA_FILE
                )

        /**
         * Postgres and ES properties are populated via [@ServiceConnection] as these datasources are mapped to standard spring boot
         * application properties
         */
        @DynamicPropertySource
        @JvmStatic
        fun dynamicProperties(registry: DynamicPropertyRegistry) {
            registry.add("app.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers)
            registry.add("app.kafka.producer.properties.schema.registry.url") { redpandaContainer.schemaRegistryAddress }
            registry.add("app.kafka.consumer.properties.schema.registry.url") { redpandaContainer.schemaRegistryAddress }
        }

        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            redpandaContainer.execInContainer(
                "/bin/sh",
                "-c",
                "curl -XPOST -H 'Content-Type: application/json' --data-binary @$SCHEMA_REGISTRY_PROFILE_SCHEMA_FILE http://localhost:$SCHEMA_REGISTRY_PORT/subjects/${ProfileCreatedEvent.SUBJECT}/versions"
            )
        }
    }

    fun fillEsIndex(containerFilePath: String = DEFAULT_PROFILES_ES_CONTAINER_FILE_PATH) {
        val result = elasticSearchContainer.execInContainer(
            "/bin/sh",
            "-c",
            "curl -XPOST -H 'Content-Type: application/x-ndjson' http://localhost:${elasticSearchContainer.exposedPorts[0]}/_bulk?refresh=true --data-binary @$containerFilePath"
        )
        assert(result.exitCode == 0) { "Failed to upload index from file $containerFilePath due to: ${result.stdout}" }
    }

    fun cleanupEsIndex(indexName: String = DEFAULT_PROFILES_ES_INDEX) {
        esOperations.indexOps(IndexCoordinates.of(indexName)).takeIf { it.exists() }?.run { delete() }
    }

    protected fun readResourcesFile(cpPath: String): InputStream = javaClass.getResourceAsStream(cpPath)

    @Suppress("UNCHECKED_CAST")
    protected fun <K : Any, V : Any> assertEventPublished(
        topic: String,
        awaitDuration: Duration = Duration.ofSeconds(5),
        expectedRecordsCount: Int = 1,
        keyDeserializer: Deserializer<K> = LongDeserializer() as Deserializer<K>,
        valueDeserializer: Deserializer<V>,
        assertion: (List<ConsumerRecord<K, V>>) -> Unit,
    ) {
        val consumer = deserializersByTopic.computeIfAbsent(topic) {
            KafkaConsumer<K, V>(
                mapOf(
                    ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to appProperties.kafka.bootstrapServers,
                    ConsumerConfig.GROUP_ID_CONFIG to "test-group",
                    ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",
                ),
                keyDeserializer,
                valueDeserializer,
            ) as KafkaConsumer<K, V>
        }.apply {
            unsubscribe()
            subscribe(listOf(topic))
        }

        try {
            val records: MutableList<ConsumerRecord<K, V>> = mutableListOf()
            await.atMost(awaitDuration).until {
                val recs = consumer.poll(Duration.ofSeconds(3))
                records += recs.records(topic) as Iterable<ConsumerRecord<K, V>>
                records.size >= expectedRecordsCount
            }

            assertion(records)
        } finally {
            consumer.unsubscribe()
        }
    }
}
