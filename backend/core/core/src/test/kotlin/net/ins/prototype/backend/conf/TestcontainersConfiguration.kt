package net.ins.prototype.backend.conf

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.elasticsearch.ElasticsearchContainer
import org.testcontainers.utility.DockerImageName
import org.testcontainers.utility.MountableFile

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

    companion object {

        const val DEFAULT_PROFILES_ES_CONTAINER_FILE_PATH = "/tmp/profiles.ndjson"
        const val DEFAULT_PROFILES_ES_INDEX = "profiles"

        @JvmStatic
        val postgresContainer: PostgreSQLContainer<*> = PostgreSQLContainer(DockerImageName.parse("postgres:17.4-alpine3.21"))
            .withUsername("test")
            .withPassword("test")
            .withDatabaseName("test")

        @JvmStatic
        val elasticSearchContainer: ElasticsearchContainer = ElasticsearchContainer(DockerImageName.parse("elasticsearch:8.17.3"))
            .withEnv("node.name", "elasticsearch")
            .withEnv("xpack.security.enabled", "false")
            .withCopyFileToContainer(MountableFile.forClasspathResource("es/profiles.ndjson"), DEFAULT_PROFILES_ES_CONTAINER_FILE_PATH)

        @DynamicPropertySource
        @JvmStatic
        fun postgresProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgresContainer::getJdbcUrl)
            registry.add("spring.datasource.username", postgresContainer::getUsername)
            registry.add("spring.datasource.password", postgresContainer::getPassword)

            registry.add("spring.elasticsearch.uris", elasticSearchContainer::getHttpHostAddress)
        }

        fun fillEsIndex(containerFilePath: String = DEFAULT_PROFILES_ES_CONTAINER_FILE_PATH) {
            val result = elasticSearchContainer.execInContainer(
                "/bin/sh", "-c", "curl -XPOST -H 'Content-Type: application/x-ndjson' http://localhost:${elasticSearchContainer.exposedPorts[0]}/_bulk?refresh=true --data-binary @$containerFilePath"
            )
            assert(result.exitCode == 0) { "Failed to upload index from file $containerFilePath due to: ${result.stdout}" }
        }

        fun cleanupEsIndex(indexName: String = DEFAULT_PROFILES_ES_INDEX) {
            val result = elasticSearchContainer.execInContainer("/bin/sh", "-c", "curl -XDELETE http://localhost:${elasticSearchContainer.exposedPorts[0]}/$indexName")
            assert(result.exitCode == 0) { "Failed to cleanup index $indexName due to ${result.stdout}" }
        }
    }

    @Bean
    @ServiceConnection
    fun postgresContainer(): PostgreSQLContainer<*> = postgresContainer

    @Bean
    @ServiceConnection
    fun elasticSearchContainer(): ElasticsearchContainer = elasticSearchContainer
}
