package net.ins.prototype.backend.profile.web.model

import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import net.ins.prototype.backend.profile.model.Interest
import net.ins.prototype.backend.profile.model.calculateMask
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class ProfileRequestTest {

    @ParameterizedTest
    @MethodSource("interests")
    @DisplayName("should mask interests")
    fun shouldMaskInterests(
        interests: List<Interest>,
        expectedCode: Int,
    ) {
        interests.calculateMask() shouldBeEqual expectedCode
    }

    @ParameterizedTest
    @MethodSource("interests")
    @DisplayName("should unmask interests")
    fun shouldUnmaskInterests(
        expectedInterests: List<Interest>,
        code: Int,
    ) {
        Interest.unmask(code) shouldContainExactlyInAnyOrder expectedInterests
    }

    companion object {

        @JvmStatic
        fun interests(): List<Arguments> = listOf(
            Arguments.of(emptyList<Interest>(), 0),

            Arguments.of(listOf(Interest.DATING), 1),
            Arguments.of(listOf(Interest.SEXTING), 4),
            Arguments.of(listOf(Interest.RELATIONSHIPS), 8),

            Arguments.of(listOf(Interest.DATING, Interest.SEXTING), 5),
            Arguments.of(listOf(Interest.SEXTING, Interest.DATING), 5),

            Arguments.of(listOf(Interest.DATING, Interest.RELATIONSHIPS), 9),
            Arguments.of(listOf(Interest.RELATIONSHIPS, Interest.DATING), 9),

            Arguments.of(listOf(Interest.SEXTING, Interest.RELATIONSHIPS), 12),
            Arguments.of(listOf(Interest.RELATIONSHIPS, Interest.SEXTING), 12),

            Arguments.of(listOf(Interest.SEXTING, Interest.DATING), 5),
            Arguments.of(listOf(Interest.DATING, Interest.SEXTING), 5),


            Arguments.of(listOf(Interest.DATING, Interest.SEXTING, Interest.RELATIONSHIPS), 13),
            Arguments.of(listOf(Interest.DATING, Interest.RELATIONSHIPS, Interest.SEXTING), 13),

            Arguments.of(listOf(Interest.RELATIONSHIPS, Interest.DATING, Interest.SEXTING), 13),
            Arguments.of(listOf(Interest.RELATIONSHIPS, Interest.SEXTING, Interest.DATING), 13),

            Arguments.of(listOf(Interest.SEXTING, Interest.DATING, Interest.RELATIONSHIPS), 13),
            Arguments.of(listOf(Interest.SEXTING, Interest.RELATIONSHIPS, Interest.DATING), 13),
        )
    }
}
