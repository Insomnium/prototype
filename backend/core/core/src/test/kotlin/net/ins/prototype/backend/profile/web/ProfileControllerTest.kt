package net.ins.prototype.backend.profile.web

import com.github.springtestdbunit.DbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DatabaseTearDown
import net.ins.prototype.backend.conf.TestcontainersConfiguration
import net.ins.prototype.backend.meta.TestProfile
import net.ins.prototype.backend.profile.dao.repo.ProfileRepository
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener
import org.springframework.test.web.servlet.MockMvc

@Import(TestcontainersConfiguration::class)
@TestExecutionListeners(
    DependencyInjectionTestExecutionListener::class,
    DbUnitTestExecutionListener::class,
)
@SpringBootTest
@AutoConfigureMockMvc
@TestProfile
@DatabaseTearDown("classpath:/dbunit/0001/profiles-cleanup.xml")
class ProfileControllerTest {

    @Autowired
    private lateinit var repo: ProfileRepository

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    @DisplayName("Should respond with found profiles")
    @DatabaseSetup("classpath:/dbunit/0001/profiles.xml")
    fun shouldFindProfiles() {
        val debug = "debug"
    }
}
