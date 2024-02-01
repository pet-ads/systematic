package br.all.domain.model.protocol

import br.all.domain.model.protocol.Criterion.CriterionType
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

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
    }
}
