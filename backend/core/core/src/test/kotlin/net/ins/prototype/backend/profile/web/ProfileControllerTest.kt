package net.ins.prototype.backend.profile.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.springtestdbunit.DbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DatabaseTearDown
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.shouldNotBeNull
import net.ins.prototype.backend.conf.TestcontainersConfiguration
import net.ins.prototype.backend.meta.TestProfile
import net.ins.prototype.backend.profile.dao.repo.ProfileRepository
import net.ins.prototype.backend.profile.model.Gender
import net.ins.prototype.backend.profile.model.Purpose
import net.ins.prototype.backend.profile.model.calculateMask
import net.ins.prototype.backend.profile.web.model.NewProfileRequest
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
import org.springframework.test.web.servlet.post
import java.time.LocalDate

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

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    @DisplayName("Should create new profile")
    fun shouldCreateProfile() {
        val newProfileRequest = NewProfileRequest(
            title = "Z",
            birth = LocalDate.of(1990, 10, 3),
            gender = Gender.MALE,
            purposes = setOf(Purpose.SEXTING, Purpose.RELATIONSHIPS, Purpose.DATING)
        )

        mockMvc.post("/v1/profiles") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(
                newProfileRequest
            )
        }.andExpect {
            status { isCreated() }
        }

        val profiles = repo.findAll()
        assertSoftly {
            profiles shouldHaveSize 1
            with(profiles.first()) {
                id.shouldNotBeNull()
                title shouldBeEqual newProfileRequest.title
                birth shouldBeEqual newProfileRequest.birth
                gender shouldBeEqual newProfileRequest.gender
                purposeMask shouldBeEqual newProfileRequest.purposes.calculateMask()
            }
        }
    }

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
