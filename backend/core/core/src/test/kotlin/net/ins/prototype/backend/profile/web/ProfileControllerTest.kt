package net.ins.prototype.backend.profile.web

import com.github.springtestdbunit.DbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DatabaseTearDown
import net.ins.prototype.backend.conf.TestcontainersConfiguration
import net.ins.prototype.backend.meta.TestProfile
import net.ins.prototype.backend.profile.dao.repo.ProfileRepository
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@Import(TestcontainersConfiguration::class)
@TestExecutionListeners(
    DependencyInjectionTestExecutionListener::class,
    DbUnitTestExecutionListener::class,
)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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
        mockMvc.get("/v1/profiles") {
            accept = MediaType.APPLICATION_JSON
            queryParam("gender", "FEMALE")
            queryParam("purposes", "DATING,SEXTING")
        }.andExpect {
            status { isOk() }
            content {
                contentType(MediaType.APPLICATION_JSON)
                jsonPath("$.profiles") { isArray() }
                jsonPath("$.profiles.length()") { value(2) }

                jsonPath("$.profiles[0].id") { value("1") }
                jsonPath("$.profiles[0].gender") { value("FEMALE") }
                jsonPath("$.profiles[0].birth") { value("1996-11-17") }
                jsonPath("$.profiles[0].purposes.length()") { value(2) }
                jsonPath("$.profiles[0].purposes") { containsInAnyOrder("DATING", "RELATIONSHIPS") }

                jsonPath("$.profiles[1].id") { value("2") }
                jsonPath("$.profiles[1].gender") { value("FEMALE") }
                jsonPath("$.profiles[1].birth") { value("1996-12-17") }
                jsonPath("$.profiles[1].purposes.length()") { value(2) }
                jsonPath("$.profiles[1].purposes") { containsInAnyOrder("DATING", "SEXTING") }
            }
        }
    }
}
