package net.ins.prototype.backend

import net.ins.prototype.backend.meta.TestProfile
import net.ins.prototype.backend.profile.dao.repo.ProfileRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchProperties
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@Import(TestcontainersConfiguration::class)
@SpringBootTest
@TestProfile
class CoreApplicationTests {

    @Autowired
    private lateinit var datasourceProperties: DataSourceProperties

    @Autowired
    private lateinit var elasticProperties: ElasticsearchProperties

    @Autowired
    private lateinit var profileRepository: ProfileRepository

    @Test
    fun contextLoads() {
        val debug = "debug"
    }
}
