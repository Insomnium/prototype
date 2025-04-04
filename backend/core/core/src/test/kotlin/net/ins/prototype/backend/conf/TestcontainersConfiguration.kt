package net.ins.prototype.backend.conf

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.elasticsearch.ElasticsearchContainer
import org.testcontainers.utility.DockerImageName

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

    companion object {

        @JvmStatic
        val postgresContainer: PostgreSQLContainer<*> = PostgreSQLContainer(DockerImageName.parse("postgres:17.4-alpine3.21"))
            .withUsername("test")
            .withPassword("test")
            .withDatabaseName("test")

        @JvmStatic
        val elasticSearchContainer: ElasticsearchContainer = ElasticsearchContainer(DockerImageName.parse("elasticsearch:8.17.3"))
            .withEnv("node.name", "elasticsearch")
            .withEnv("xpack.security.enabled", "false")

        @DynamicPropertySource
        @JvmStatic
        fun postgresProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgresContainer::getJdbcUrl)
            registry.add("spring.datasource.username", postgresContainer::getUsername)
            registry.add("spring.datasource.password", postgresContainer::getPassword)

            registry.add("spring.elasticsearch.uris", elasticSearchContainer::getHttpHostAddress)
        }
    }

    @Bean
    @ServiceConnection
    fun postgresContainer(): PostgreSQLContainer<*> = postgresContainer

    @Bean
    @ServiceConnection
    fun elasticSearchContainer(): ElasticsearchContainer = elasticSearchContainer
}
