package br.all.domain.model.protocol

import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.assertEquals

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

        @Test
        fun `should create a valid PICOC with a specified context`() {
            assertDoesNotThrow {
                Picoc(
                    "Population",
                    "Intervention",
                    "Control",
                    "Outcome",
                    "Context",
                )
            }
        }
    }

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("When invalid arguments are provided")
    inner class WhenInvalidArgumentsAreProvided {
        @ParameterizedTest(name = "[{index}] population=\"{0}\"")
        @ValueSource(strings = ["", " ", "   "])
        fun `should not create a PICOC with blank population`(population: String) {
            val exception = assertThrows<IllegalArgumentException> {
                Picoc(
                    population,
                    "Intervention",
                    "Control",
                    "Outcome",
                    "Context",
                )
            }
            assertEquals("The population described in the PICOC must not be blank!", exception.message)
        }
    }
}
