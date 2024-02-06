package br.all.domain.model.protocol

import org.junit.jupiter.api.*

@Tag("UnitTest")
class PicocTest {
    @Nested
    @Tag("ValidClasses")
    @DisplayName("When providing valid entries")
    inner class WhenProvidingValidEntries {
        @Test
        fun `should create a valid PICOC without context`() {
            assertDoesNotThrow {
                Picoc(
                    "Population",
                    "Intervention",
                    "Control",
                    "Outcome",
                )
            }
        }
    }
}
