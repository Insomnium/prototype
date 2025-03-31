package net.ins.prototype.backend.meta

import org.springframework.test.context.ActiveProfiles

@ActiveProfiles(TestProfile.PROFILE)
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class TestProfile {

    companion object {
        const val PROFILE = "test"
    }
}
