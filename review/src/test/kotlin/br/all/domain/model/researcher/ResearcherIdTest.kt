package br.all.domain.model.researcher

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.util.*

@Tag("UnitTest")
class ResearcherIdTest {

    @Nested
    @DisplayName("Valid classes")
    inner class ValidClasses {

        @Test
        fun `create ResearcherId with valid UUID`() {
            val validUUID = UUID.randomUUID()

            val researcherId = ResearcherId(validUUID)

            assertEquals(validUUID, researcherId.value)
        }

        @Test
        fun `validate ResearcherId`() {
            val validUUID = UUID.randomUUID()
            val researcherId = ResearcherId(validUUID)

            val notification = researcherId.validate()

            assertTrue(notification.hasNoErrors())
        }

        @Test
        fun `get value from ResearcherId`() {
            val validUUID = UUID.randomUUID()
            val researcherId = ResearcherId(validUUID)

            val result = researcherId.value()

            assertEquals(validUUID, result)
        }

    }

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("Invalid constructor classes")
    inner class InvalidConstructorClasses {

        @Test
        fun `create ResearcherId with invalid UUID`() {
            assertThrows<IllegalArgumentException> { ResearcherId(UUID.fromString("invalid_uuid")) }
        }
    }
}
