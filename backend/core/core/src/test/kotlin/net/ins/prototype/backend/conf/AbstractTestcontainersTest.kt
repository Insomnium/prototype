package net.ins.prototype.backend.conf

import net.ins.prototype.backend.profile.event.ProfileCreatedEvent
import org.junit.jupiter.api.BeforeAll
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.elasticsearch.ElasticsearchContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.kafka.ConfluentKafkaContainer
import org.testcontainers.redpanda.RedpandaContainer
import org.testcontainers.utility.DockerImageName
import org.testcontainers.utility.MountableFile

@Testcontainers
open class AbstractTestcontainersTest {

    companion object {

        const val DEFAULT_PROFILES_ES_CONTAINER_FILE_PATH = "/tmp/profiles.ndjson"
        const val DEFAULT_PROFILES_ES_INDEX = "profiles"

        const val SCHEMA_REGISTRY_PROFILE_SCHEMA_FILE = "/tmp/profile.json"
        const val SCHEMA_REGISTRY_PORT = 8081

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
                .withCopyFileToContainer(MountableFile.forClasspathResource("kafka/schema/profile.json"), SCHEMA_REGISTRY_PROFILE_SCHEMA_FILE)

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

        fun fillEsIndex(containerFilePath: String = DEFAULT_PROFILES_ES_CONTAINER_FILE_PATH) {
            val result = elasticSearchContainer.execInContainer(
                "/bin/sh",
                "-c",
                "curl -XPOST -H 'Content-Type: application/x-ndjson' http://localhost:${elasticSearchContainer.exposedPorts[0]}/_bulk?refresh=true --data-binary @$containerFilePath"
            )
            assert(result.exitCode == 0) { "Failed to upload index from file $containerFilePath due to: ${result.stdout}" }
        }

        fun cleanupEsIndex(indexName: String = DEFAULT_PROFILES_ES_INDEX) {
            val result = elasticSearchContainer.execInContainer(
                "/bin/sh",
                "-c",
                "curl -XDELETE http://localhost:${elasticSearchContainer.exposedPorts[0]}/$indexName"
            )
            assert(result.exitCode == 0) { "Failed to cleanup index $indexName due to ${result.stdout}" }
        }
    }
}
