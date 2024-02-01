package br.all.domain.model.protocol

import br.all.domain.model.protocol.Criterion.CriterionType
import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
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
    }
}
