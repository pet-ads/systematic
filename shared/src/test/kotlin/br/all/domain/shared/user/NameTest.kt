package br.all.domain.shared.user

import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

@Tag("UnitTest")
class NameTest {

    @Nested
    @DisplayName("when name is valid")
    inner class ValidNameTest {
        @Test
        fun `should create Name for simple name`() {
            assertDoesNotThrow { Name("John Doe") }
        }

        @Test
        fun `should create Name with accented characters`() {
            assertDoesNotThrow { Name("João Pacífico") }
        }

        @Test
        fun `should create Name with dots`() {
            assertDoesNotThrow { Name("M. John Doe") }
        }

        @Test
        fun `should create Name with apostrophes`() {
            assertDoesNotThrow { Name("O'Connell") }
        }

        @Test
        fun `should create Name with a mix of valid characters`() {
            assertDoesNotThrow { Name("M. O'Connell Pacífico") }
        }
    }

    @Nested
    @DisplayName("when name is invalid")
    inner class InvalidNameTest {
        @Test
        fun `should throw exception for empty name`() {
            val exception = assertThrows<IllegalArgumentException> { Name("") }
            assertTrue(exception.message!!.contains("The name must not be blank!"))
        }

        @Test
        fun `should throw exception for blank name`() {
            val exception = assertThrows<IllegalArgumentException> { Name("   ") }
            assertTrue(exception.message!!.contains("The name must not be blank!"))
        }

        @Test
        fun `should throw exception for name starting with a space`() {
            val exception = assertThrows<IllegalArgumentException> { Name(" John Doe") }
            assertEquals("The name must not start or end with blank spaces!", exception.message)
        }

        @Test
        fun `should throw exception for name ending with a space`() {
            val exception = assertThrows<IllegalArgumentException> { Name("John Doe ") }
            assertEquals("The name must not start or end with blank spaces!", exception.message)
        }

        @Test
        fun `should throw exception for name with numbers`() {
            val exception = assertThrows<IllegalArgumentException> { Name("John Doe 123") }
            assertEquals("The name must contain only letters, dots, apostrophes, and blank spaces!", exception.message)
        }

        @Test
        fun `should throw exception for name with invalid symbols`() {
            val exception = assertThrows<IllegalArgumentException> { Name("John-Doe@") }
            assertEquals("The name must contain only letters, dots, apostrophes, and blank spaces!", exception.message)
        }
    }

    @Nested
    @DisplayName("equality checks")
    inner class EqualityTest {
        @Test
        fun `should be equal when two Name objects have the same value`() {
            val name1 = Name("M. Pacífico")
            val name2 = Name("M. Pacífico")
            assertEquals(name1, name2)
        }

        @Test
        fun `should not be equal when two Name objects have different values`() {
            val name1 = Name("John Doe")
            val name2 = Name("Jane Doe")
            assertNotEquals(name1, name2)
        }
    }
}
