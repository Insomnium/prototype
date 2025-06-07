package net.ins.prototype.backend.profile.web.model

import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.equals.shouldBeEqual
import net.ins.prototype.backend.profile.model.Purpose
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
        purposes: List<Purpose>,
        expectedCode: Int,
    ) {
        purposes.calculateMask() shouldBeEqual expectedCode
    }

    @ParameterizedTest
    @MethodSource("interests")
    @DisplayName("should unmask interests")
    fun shouldUnmaskInterests(
        expectedPurposes: List<Purpose>,
        code: Int,
    ) {
        Purpose.unmask(code) shouldContainExactlyInAnyOrder expectedPurposes
    }

    companion object {

        @JvmStatic
        fun interests(): List<Arguments> = listOf(
            Arguments.of(emptyList<Purpose>(), 0),

            Arguments.of(listOf(Purpose.DATING), 1),
            Arguments.of(listOf(Purpose.SEXTING), 4),
            Arguments.of(listOf(Purpose.RELATIONSHIPS), 8),

            Arguments.of(listOf(Purpose.DATING, Purpose.SEXTING), 5),
            Arguments.of(listOf(Purpose.SEXTING, Purpose.DATING), 5),

            Arguments.of(listOf(Purpose.DATING, Purpose.RELATIONSHIPS), 9),
            Arguments.of(listOf(Purpose.RELATIONSHIPS, Purpose.DATING), 9),

            Arguments.of(listOf(Purpose.SEXTING, Purpose.RELATIONSHIPS), 12),
            Arguments.of(listOf(Purpose.RELATIONSHIPS, Purpose.SEXTING), 12),

            Arguments.of(listOf(Purpose.SEXTING, Purpose.DATING), 5),
            Arguments.of(listOf(Purpose.DATING, Purpose.SEXTING), 5),


            Arguments.of(listOf(Purpose.DATING, Purpose.SEXTING, Purpose.RELATIONSHIPS), 13),
            Arguments.of(listOf(Purpose.DATING, Purpose.RELATIONSHIPS, Purpose.SEXTING), 13),

            Arguments.of(listOf(Purpose.RELATIONSHIPS, Purpose.DATING, Purpose.SEXTING), 13),
            Arguments.of(listOf(Purpose.RELATIONSHIPS, Purpose.SEXTING, Purpose.DATING), 13),

            Arguments.of(listOf(Purpose.SEXTING, Purpose.DATING, Purpose.RELATIONSHIPS), 13),
            Arguments.of(listOf(Purpose.SEXTING, Purpose.RELATIONSHIPS, Purpose.DATING), 13),
        )
    }
}
