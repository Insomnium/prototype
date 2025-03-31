package net.ins.prototype.backend.profile.web

import com.github.springtestdbunit.annotation.DatabaseSetup
import net.ins.prototype.backend.TestcontainersConfiguration
import net.ins.prototype.backend.meta.TestProfile
import net.ins.prototype.backend.profile.dao.repo.ProfileRepository
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@Import(TestcontainersConfiguration::class)
@SpringBootTest
@TestProfile
class ProfileControllerTest {

    @Autowired
    private lateinit var repo: ProfileRepository

    @Test
    @DisplayName("Should respond with found profiles")
    @DatabaseSetup("")
    fun shouldFindProfiles() {

    }
}
