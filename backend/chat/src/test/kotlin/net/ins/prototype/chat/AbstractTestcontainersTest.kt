package net.ins.prototype.chat

import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.cassandra.CassandraContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.kafka.ConfluentKafkaContainer
import org.testcontainers.utility.DockerImageName

@Testcontainers
abstract class AbstractTestcontainersTest {

    companion object {

        const val CASSANDRA_DATACENTER = "prototype-dc1"

        @JvmStatic
        @Container
        @ServiceConnection
        val cassandraContainer = CassandraContainer("cassandra:5.0.5")
            .withEnv("CASSANDRA_DC", "prototype-dc1")
            .withInitScript("cassandra/init-chat.cql")

        @JvmStatic
        @Container
        private val kafkaContainer = ConfluentKafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.8.0"))

        @DynamicPropertySource
        @JvmStatic
        fun dynamicProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.cassandra.contact-points") { cassandraContainer.contactPoints }
            registry.add("spring.cassandra.local-datacenter") { CASSANDRA_DATACENTER }
            registry.add("spring.liquibase.url") { "jdbc:cassandra://${cassandraContainer.contactPoints}/chat?compliancemode=Liquibase&localdatacenter=$CASSANDRA_DATACENTER" }
        }

        private val CassandraContainer.contactPoints: String
            get() = "$host:$firstMappedPort"
    }
}