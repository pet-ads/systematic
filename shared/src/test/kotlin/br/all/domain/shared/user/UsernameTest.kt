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
class UsernameTest {

    @Nested
    @DisplayName("when username is valid")
    inner class ValidUsernameTest {
        @Test
        fun `should create Username with letters and numbers`() {
            assertDoesNotThrow { Username("user123") }
        }

        @Test
        fun `should create Username with dashes`() {
            assertDoesNotThrow { Username("user-name") }
        }

        @Test
        fun `should create Username with underscores`() {
            assertDoesNotThrow { Username("user_name") }
        }

        @Test
        fun `should create Username with a mix of valid characters`() {
            assertDoesNotThrow { Username("user-123_name") }
        }
    }

    @Nested
    @DisplayName("when username is invalid")
    inner class InvalidUsernameTest {
        @Test
        fun `should throw exception for empty username`() {
            val exception = assertThrows<IllegalArgumentException> { Username("") }
            assertTrue(exception.message!!.contains("Username must not be blank!"))
        }

        @Test
        fun `should throw exception for blank username`() {
            val exception = assertThrows<IllegalArgumentException> { Username("   ") }
            assertTrue(exception.message!!.contains("Username must not be blank!"))
        }

        @Test
        fun `should throw exception for username with spaces`() {
            val exception = assertThrows<IllegalArgumentException> { Username("user name") }
            assertEquals("Username must contain only letters and numbers, dashes and underscores!", exception.message)
        }

        @Test
        fun `should throw exception for username with invalid symbols`() {
            val exception = assertThrows<IllegalArgumentException> { Username("user@name!") }
            assertEquals("Username must contain only letters and numbers, dashes and underscores!", exception.message)
        }

        @Test
        fun `should throw exception for username with dots`() {
            val exception = assertThrows<IllegalArgumentException> { Username("user.name") }
            assertEquals("Username must contain only letters and numbers, dashes and underscores!", exception.message)
        }
    }

    @Nested
    @DisplayName("equality checks")
    inner class EqualityTest {
        @Test
        fun `should be equal when two Username objects have the same value`() {
            val user1 = Username("test-user_1")
            val user2 = Username("test-user_1")
            assertEquals(user1, user2)
        }

        @Test
        fun `should not be equal when two Username objects have different values`() {
            val user1 = Username("user1")
            val user2 = Username("user2")
            assertNotEquals(user1, user2)
        }
    }
}