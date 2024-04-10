package br.all.domain.model.protocol

import br.all.domain.model.protocol.Criterion.CriterionType
import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.assertEquals

@Tag("UnitTest")
class CriterionTest {
    @Nested
    @Tag("ValidClasses")
    @DisplayName("When creating criteria successfully")
    inner class WhenCreatingCriteriaSuccessfully {
        @ParameterizedTest
        @EnumSource(value = CriterionType::class)
        fun `should create any kind of criteria`(type: CriterionType) {
            assertDoesNotThrow { Criterion("Valid description", type) }
        }

        @Test
        fun `should create exclusion criteria with valid description`() {
            val criterion = assertDoesNotThrow { Criterion.toExclude("Valid description") }
            assertEquals(CriterionType.EXCLUSION, criterion.type)
        }

        @Test
        fun `should create inclusion criteria with valid description`() {
            val criterion = assertDoesNotThrow { Criterion.toInclude("Valid description") }
            assertEquals(CriterionType.INCLUSION, criterion.type)
        }
    }

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("When providing invalid descriptions")
    inner class WhenProvidingInvalidDescriptions {
        @ParameterizedTest(name = "[{index}]: description=\"{0}\"")
        @ValueSource(strings = ["", " ", "  "])
        fun `should not allow any kind of blank string as criterion descriptions`(description: String) {
            assertAll(
                { assertThrows<IllegalArgumentException> { Criterion(description, CriterionType.INCLUSION) } },
                { assertThrows<IllegalArgumentException> { Criterion(description, CriterionType.EXCLUSION) } },
            )
        }

        @ParameterizedTest(name = "[{index}]: description=\"{0}\"")
        @ValueSource(strings = ["", " ", "  "])
        fun `should not create inclusion criteria with blank strings`(description: String) {
            assertThrows<IllegalArgumentException> { Criterion.toInclude(description) }
        }

        @ParameterizedTest(name = "[{index}]: description=\"{0}\"")
        @ValueSource(strings = ["", " ", "  "])
        fun `should not created exclusion criteria with blank strings`(description: String) {
            assertThrows<IllegalArgumentException> { Criterion.toExclude(description) }
        }
    }
}
