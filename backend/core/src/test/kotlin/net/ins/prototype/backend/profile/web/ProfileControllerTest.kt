package net.ins.prototype.backend.profile.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.springtestdbunit.DbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DatabaseTearDown
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.string.shouldStartWith
import net.ins.prototype.backend.conf.AbstractTestcontainersTest
import net.ins.prototype.backend.image.dao.repo.ImageRepository
import net.ins.prototype.backend.meta.TestProfile
import net.ins.prototype.backend.profile.dao.model.ProfileEntity
import net.ins.prototype.backend.profile.dao.repo.ProfileRepository
import net.ins.prototype.backend.profile.event.ProfileCreatedEvent
import net.ins.prototype.backend.profile.event.ProfileEvent
import net.ins.prototype.backend.profile.event.ProfileUpdatedEvent
import net.ins.prototype.backend.profile.model.Gender
import net.ins.prototype.backend.profile.model.Purpose
import net.ins.prototype.backend.profile.model.calculateMask
import net.ins.prototype.backend.profile.web.model.CreateProfileRequest
import net.ins.prototype.backend.profile.web.model.UpdateProfileRequest
import org.apache.kafka.common.serialization.Deserializer
import org.awaitility.kotlin.await
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.reset
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.multipart
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import java.nio.file.Path
import java.time.Duration
import java.time.LocalDate

@Import(AbstractTestcontainersTest::class)
@TestExecutionListeners(
    DependencyInjectionTestExecutionListener::class,
    DbUnitTestExecutionListener::class,
)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestProfile
@DatabaseTearDown("classpath:/dbunit/0001/cleanup.xml")
class ProfileControllerTest : AbstractTestcontainersTest() {

    @MockitoSpyBean
    @Autowired
    private lateinit var profileRepo: ProfileRepository

    @Autowired
    private lateinit var imageRepo: ImageRepository

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var profileEventDeserializer: Deserializer<ProfileEvent>

    @BeforeEach
    fun beforeEach() {
        fillEsIndex()
    }

    @AfterEach
    fun tearDown() {
        reset(profileRepo, esOperations)
        cleanupEsIndex()
    }

    companion object {
        const val SAMPLE_FEMALE_PROFILE_ID = 1
        const val SAMPLE_MALE_PROFILE_ID = 5
    }

    @Test
    @DisplayName("Should create new profile")
    fun shouldCreateProfile() {
        cleanupEsIndex()

        val newProfileRequest = CreateProfileRequest(
            title = "Z",
            birth = LocalDate.of(1990, 10, 3),
            gender = Gender.MALE,
            countryId = "RU",
            purposes = setOf(Purpose.SEXTING, Purpose.RELATIONSHIPS, Purpose.DATING),
        )

        mockMvc.post("/v1/profiles") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(newProfileRequest)
        }.andExpect {
            status { isCreated() }
        }

        val profiles = profileRepo.findAll()
        profiles shouldHaveSize 1

        assertEventPublished<Long, ProfileEvent>(
            topic = appProperties.integrations.topics.profiles.name,
            valueDeserializer = profileEventDeserializer,
        ) {
            assertSoftly {
                it shouldHaveSize 1
                val profileCreatedEvent = it.first().value() as ProfileCreatedEvent
                profileCreatedEvent.gender shouldBeEqual newProfileRequest.gender
                profileCreatedEvent.birth shouldBeEqual newProfileRequest.birth
                profileCreatedEvent.countryId shouldBeEqual newProfileRequest.countryId
                profileCreatedEvent.purposes shouldContainExactlyInAnyOrder newProfileRequest.purposes
                profileCreatedEvent.dbId shouldBeEqual profiles.first().id!!
            }
        }

        await.atMost(Duration.ofSeconds(5)).until {
            profileRepo.findByIdOrNull(profileRepo.findAll().first().id!!)?.lastIndexedAt != null
        }
        with(profileRepo.findAll().first()) {
            createdAt shouldBeLessThan requireNotNull(lastIndexedAt)
            id.shouldNotBeNull()
            title shouldBeEqual newProfileRequest.title
            birth shouldBeEqual newProfileRequest.birth
            gender shouldBeEqual newProfileRequest.gender
            purposeMask shouldBeEqual newProfileRequest.purposes.calculateMask()
            indexId.shouldNotBeNull()
        }
    }

    @Test
    @DisplayName("Should update existing profile")
    @DatabaseSetup("classpath:/dbunit/0001/profiles-single.xml")
    fun shouldUpdateProfile() {
        fillEsIndex()
        val profileId: Long = 1
        val existentProfileLastIndexedAt = profileRepo.findAll().first().lastIndexedAt!!
        val profileUpdateRequest = UpdateProfileRequest(
            title = "XXX",
            birth = LocalDate.of(1990, 1, 1),
            gender = Gender.FEMALE,
            countryId = "BY",
            purposes = setOf(Purpose.RELATIONSHIPS),
        )
        mockMvc.put("/v1/profiles/$profileId") {
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(profileUpdateRequest)
        }.andExpect {
            status { isAccepted() }
        }

        val profiles = profileRepo.findAll()
        profiles shouldHaveSize 1

        assertEventPublished<Long, ProfileEvent>(
            topic = appProperties.integrations.topics.profiles.name,
            valueDeserializer = profileEventDeserializer,
            awaitDuration = Duration.ofSeconds(5000),
        ) {
            assertSoftly {
                it shouldHaveSize 1
                val profileUpdatedEvent = it.first().value() as ProfileUpdatedEvent
                profileUpdatedEvent.gender shouldBeEqual profileUpdateRequest.gender
                profileUpdatedEvent.birth shouldBeEqual profileUpdateRequest.birth
                profileUpdatedEvent.countryId shouldBeEqual profileUpdatedEvent.countryId
                profileUpdatedEvent.purposes shouldContainExactlyInAnyOrder profileUpdateRequest.purposes
                profileUpdatedEvent.dbId shouldBeEqual profiles.first().id!!
            }
        }

        await.atMost(Duration.ofSeconds(5)).until {
            profileRepo.findAll().first().lastIndexedAt!! > existentProfileLastIndexedAt
        }
        with(profileRepo.findAll().first()) {
            title shouldBeEqual profileUpdateRequest.title
            birth shouldBeEqual profileUpdateRequest.birth
            countryId shouldBeEqual profileUpdateRequest.countryId
            purposeMask shouldBeEqual profileUpdateRequest.purposes.calculateMask()
        }
    }

    @Test
    @DisplayName("Should not allow to change gender")
    @DatabaseSetup("classpath:/dbunit/0001/profiles-single.xml")
    fun shouldDenyChangingGender() {
        fillEsIndex()
        val profileId: Long = 1
        val profileUpdateRequest = UpdateProfileRequest(
            title = "XXX",
            birth = LocalDate.of(1990, 1, 1),
            gender = Gender.MALE,
            countryId = "BY",
            purposes = setOf(Purpose.RELATIONSHIPS),
        )
        mockMvc.put("/v1/profiles/$profileId") {
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(profileUpdateRequest)
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    @DisplayName("Should respond with error on same gender search")
    @DatabaseSetup("classpath:/dbunit/0001/profiles.xml")
    fun shouldForbidSameGenderSearch() {
        mockMvc.get("/v1/profiles") {
            accept = MediaType.APPLICATION_JSON
            queryParam("gender", "FEMALE")
            queryParam("purposes", "DATING,SEXTING")
            queryParam("countryId", "RU")
            header("x-user-id", SAMPLE_FEMALE_PROFILE_ID)
        }.andExpect { status { isBadRequest() } }
    }

    @Test
    @DisplayName("Should find profiles in DB when no index exists")
    @DatabaseSetup("classpath:/dbunit/0001/profiles.xml")
    fun shouldFindProfilesBasedOnEsIndex() {
        mockMvc.get("/v1/profiles") {
            accept = MediaType.APPLICATION_JSON
            queryParam("gender", "FEMALE")
            queryParam("purposes", "DATING,SEXTING")
            queryParam("countryId", "RU")
            header("x-user-id", SAMPLE_MALE_PROFILE_ID)
        }.andExpect {
            status { isOk() }
            content {
                contentType(MediaType.APPLICATION_JSON)
                jsonPath("$.results") { isArray() }
                jsonPath("$.results.length()") { value(3) }

                jsonPath("$.results[0].id") { value("1") }
                jsonPath("$.results[0].gender") { value("FEMALE") }
                jsonPath("$.results[0].title") { value("A") }
                jsonPath("$.results[0].birth") { value("1996-11-17") }
                jsonPath("$.results[0].countryId") { value("RU") }
                jsonPath("$.results[0].purposes.length()") { value(2) }
                jsonPath("$.results[0].purposes") { containsInAnyOrder("DATING", "RELATIONSHIPS") }

                jsonPath("$.results[1].id") { value("2") }
                jsonPath("$.results[1].gender") { value("FEMALE") }
                jsonPath("$.results[1].title") { value("B") }
                jsonPath("$.results[1].birth") { value("1996-12-17") }
                jsonPath("$.results[1].countryId") { value("RU") }
                jsonPath("$.results[1].purposes.length()") { value(2) }
                jsonPath("$.results[1].purposes") { containsInAnyOrder("DATING", "SEXTING") }

                jsonPath("$.results[2].id") { value("3") }
                jsonPath("$.results[2].gender") { value("FEMALE") }
                jsonPath("$.results[2].title") { value("C") }
                jsonPath("$.results[2].birth") { value("1997-12-17") }
                jsonPath("$.results[2].countryId") { value("RU") }
                jsonPath("$.results[2].purposes.length()") { value(2) }
                jsonPath("$.results[2].purposes") { containsInAnyOrder("SEXTING", "RELATIONSHIPS") }
            }
        }

        verify(profileRepo) {
            1 * { findAllById(any()) }
            0 * { findAll(any<Specification<ProfileEntity>>()) }
        }
    }

    @Test
    @DisplayName("Should find profiles by ES index")
    @DatabaseSetup("classpath:/dbunit/0001/profiles.xml")
    fun shouldFindProfilesWithDbInIndexAbsence() {
        cleanupEsIndex()

        mockMvc.get("/v1/profiles") {
            accept = MediaType.APPLICATION_JSON
            queryParam("gender", "FEMALE")
            queryParam("purposes", "DATING,SEXTING")
            queryParam("countryId", "RU")
            header("x-user-id", SAMPLE_MALE_PROFILE_ID)
        }.andExpect {
            status { isOk() }
            content {
                contentType(MediaType.APPLICATION_JSON)
                jsonPath("$.results") { isArray() }
                jsonPath("$.results.length()") { value(3) }

                jsonPath("$.results[0].id") { value("1") }
                jsonPath("$.results[0].gender") { value("FEMALE") }
                jsonPath("$.results[0].title") { value("A") }
                jsonPath("$.results[0].birth") { value("1996-11-17") }
                jsonPath("$.results[0].countryId") { value("RU") }
                jsonPath("$.results[0].purposes.length()") { value(2) }
                jsonPath("$.results[0].purposes") { containsInAnyOrder("DATING", "RELATIONSHIPS") }

                jsonPath("$.results[1].id") { value("2") }
                jsonPath("$.results[1].gender") { value("FEMALE") }
                jsonPath("$.results[1].title") { value("B") }
                jsonPath("$.results[1].birth") { value("1996-12-17") }
                jsonPath("$.results[1].countryId") { value("RU") }
                jsonPath("$.results[1].purposes.length()") { value(2) }
                jsonPath("$.results[1].purposes") { containsInAnyOrder("DATING", "SEXTING") }

                jsonPath("$.results[2].id") { value("3") }
                jsonPath("$.results[2].gender") { value("FEMALE") }
                jsonPath("$.results[2].title") { value("C") }
                jsonPath("$.results[2].birth") { value("1997-12-17") }
                jsonPath("$.results[2].countryId") { value("RU") }
                jsonPath("$.results[2].purposes.length()") { value(2) }
                jsonPath("$.results[2].purposes") { containsInAnyOrder("SEXTING", "RELATIONSHIPS") }
            }
        }

        verify(profileRepo) {
            0 * { findAllById(any()) }
            1 * { findAll(any<Specification<ProfileEntity>>()) }
        }
    }

    @Test
    @DisplayName("Should respond with profiles found based on ES index")
    @DatabaseSetup("classpath:/dbunit/0001/profiles.xml")
    fun shouldFindProfilesWithNoSpecificPurposes() {
        mockMvc.get("/v1/profiles") {
            accept = MediaType.APPLICATION_JSON
            queryParam("gender", "FEMALE")
            header("x-user-id", SAMPLE_MALE_PROFILE_ID)
        }.andExpect {
            status { isOk() }
            jsonPath("$.results.length()") { value(4) }
        }

        verify(profileRepo) {
            1 * { findAllById(any()) }
            0 * { findAll(any<Specification<ProfileEntity>>()) }
        }
    }

    @Test
    @DisplayName("Should respond with empty profiles array when none found")
    @DatabaseSetup("classpath:/dbunit/0001/profiles.xml")
    fun shouldRespondWithNoProfilesWhenNoneMatches() {
        mockMvc.get("/v1/profiles") {
            accept = MediaType.APPLICATION_JSON
            queryParam("gender", "MALE")
            queryParam("purposes", "RELATIONSHIPS")
            header("x-user-id", SAMPLE_FEMALE_PROFILE_ID)
        }.andExpect {
            status { isOk() }
            jsonPath("$.results.length()") { value(0) }
        }
    }

    @Test
    @DisplayName("Should respond with error on image uploading for non-existing profile")
    @DatabaseSetup("classpath:/dbunit/0001/profiles.xml")
    fun shouldNotUploadImageForNonExistingProfile() {
        mockMvc.multipart("/v1/profiles/999/images") {
            contentType = MediaType.IMAGE_PNG
            file(MockMultipartFile("file", readResourcesFile("/images/fox.png")))
        }.andExpect {
            status { isNotFound() }
        }
    }

    @Test
    @DisplayName("Should successfully upload primary profile photo")
    @DatabaseSetup("classpath:/dbunit/0001/profiles.xml")
    fun shouldUploadPrimaryProfilePhoto() {
        val profileId: Long = 1
        mockMvc.multipart("/v1/profiles/$profileId/images") {
            contentType = MediaType.IMAGE_PNG
            file(MockMultipartFile("file", readResourcesFile("/images/fox.png")))
        }.andExpect {
            status { isOk() }
        }

        assertSoftly {
            imageRepo.count() shouldBeEqual 1
            val image = imageRepo.findAll().first()
            image.id.shouldNotBeNull()
            image.primary shouldBeEqual true
            image.profileId shouldBeEqual profileId
            image.approved shouldBeEqual false
            image.hidden shouldBeEqual false
            image.primary shouldBeEqual true
            image.internalFileName.shouldNotBeNull()
            image.folderUri shouldStartWith Path.of(appProperties.images.fsBaseUri, profileId.toString()).toAbsolutePath().toString()
            image.cdnUri shouldBeEqual "${appProperties.images.cdnBaseUri}/$profileId/${image.internalFileName}"
        }
    }

    @Test
    @DisplayName("Should successfully upload secondary profile photo")
    @DatabaseSetup(
        "classpath:/dbunit/0001/profiles.xml",
        "classpath:/dbunit/0001/images.xml",
    )
    fun shouldUploadSecondaryProfilePhoto() {
        val profileId: Long = 1
        mockMvc.multipart("/v1/profiles/$profileId/images") {
            contentType = MediaType.IMAGE_PNG
            file(MockMultipartFile("file", readResourcesFile("/images/fox.png")))
        }.andExpect {
            status { isOk() }
        }

        assertSoftly {
            imageRepo.count() shouldBeEqual 2
            val images = imageRepo.findAll()
            images shouldHaveSize 2
            images.count { it.primary } shouldBeEqual 1
            images.count { !it.primary } shouldBeEqual 1
        }
    }

    @Test
    @DisplayName("Should respond with all profile images")
    @DatabaseSetup(
        "classpath:/dbunit/0001/profiles.xml",
        "classpath:/dbunit/0001/images-multiple.xml",
    )
    fun shouldGetAllTheProfilePhotos() {
        val profileId: Long = 1
        mockMvc.get("/v1/profiles/$profileId/images") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content {
                contentType(MediaType.APPLICATION_JSON)
                jsonPath("$.results") { isArray() }
                jsonPath("$.results.length()") { value(3) }

                jsonPath("$.results[0].id") { value("0") }
                jsonPath("$.results[0].cdnUri") { value("https://some.cdn/image1.jpg") }
                jsonPath("$.results[0].primary") { value("true") }

                jsonPath("$.results[1].id") { value("1") }
                jsonPath("$.results[1].cdnUri") { value("https://some.cdn/image2.jpg") }
                jsonPath("$.results[1].primary") { value("false") }

                jsonPath("$.results[2].id") { value("2") }
                jsonPath("$.results[2].cdnUri") { value("https://some.cdn/image3.jpg") }
                jsonPath("$.results[2].primary") { value("false") }
            }
        }
    }

    @Test
    @DisplayName("Should respond with empty images array when none")
    @DatabaseSetup(
        "classpath:/dbunit/0001/profiles.xml",
        "classpath:/dbunit/0001/images-multiple.xml",
    )
    fun shouldRespondWithNoProfilePhotos() {
        val profileId: Long = 2
        mockMvc.get("/v1/profiles/$profileId/images") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content {
                contentType(MediaType.APPLICATION_JSON)
                jsonPath("$.results") { isArray() }
                jsonPath("$.results.length()") { value(0) }
            }
        }
    }

    @Test
    @DisplayName("Should remove primary image and mark the next uploaded as of same type")
    @DatabaseSetup(
        "classpath:/dbunit/0001/profiles.xml",
        "classpath:/dbunit/0001/images-multiple.xml",
    )
    fun shouldDeletePrimaryProfilePhoto() {
        val profileId: Long = 1
        val primaryImageId: Long = 0
        mockMvc.delete("/v1/profiles/$profileId/images/$primaryImageId") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isOk() } }

        val profileImages = imageRepo.findAll()
        assertSoftly {
            profileImages shouldHaveSize 2
            profileImages.count { it.primary } shouldBeEqual 1
            profileImages.count { !it.primary } shouldBeEqual 1
            profileImages.single { requireNotNull(it.id) == 1L }.primary shouldBeEqual true
        }
    }

    @Test
    @DisplayName("Should remove primary image and mark the next uploaded as of same type")
    @DatabaseSetup(
        "classpath:/dbunit/0001/profiles.xml",
        "classpath:/dbunit/0001/images-multiple.xml",
    )
    fun shouldDeleteNonPrimaryProfilePhoto() {
        val profileId: Long = 1
        val nonPrimaryImageId: Long = 2
        mockMvc.delete("/v1/profiles/$profileId/images/$nonPrimaryImageId") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isOk() } }

        val profileImages = imageRepo.findAll()
        assertSoftly {
            profileImages shouldHaveSize 2
            profileImages.count { it.primary } shouldBeEqual 1
            profileImages.count { !it.primary } shouldBeEqual 1
            profileImages.single { requireNotNull(it.id) == 0L }.primary shouldBeEqual true
        }
    }

    @Test
    @DisplayName("Should respond with 404 when profile image not found")
    @DatabaseSetup(
        "classpath:/dbunit/0001/profiles.xml",
        "classpath:/dbunit/0001/images-multiple.xml",
    )
    fun shouldFailOnRemovingNonExistentProfilePhoto() {
        val profileId: Long = 1
        val nonExistentImageId: Long = 999
        mockMvc.delete("/v1/profiles/$profileId/images/$nonExistentImageId") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isNotFound() } }

        val profileImages = imageRepo.findAll()
        assertSoftly {
            profileImages shouldHaveSize 3
            profileImages.count { it.primary } shouldBeEqual 1
            profileImages.count { !it.primary } shouldBeEqual 2
            profileImages.single { requireNotNull(it.id) == 0L }.primary shouldBeEqual true
        }
    }
}
