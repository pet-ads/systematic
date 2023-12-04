package br.all.domain.shared.ddd

import br.all.domain.shared.valueobject.Email
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class EmailTest {

    @Test
    fun `valid EMAIL should not throw an exception`() {
        assertDoesNotThrow { Email("email@test.com") }
    }

    @Test
    fun `equal emails should be equal`() {
        val email1 = Email("email@test.com")
        val email2 = Email("email@test.com")
        assertEquals(email1, email2)
    }

    @Test
    fun `different emails should not be equal`() {
        val email1 = Email("email@test.com")
        val email2 = Email("another_email@test.com")
        assertNotEquals(email1, email2)
    }

    @Test
    fun `empty email should throw an exception`() {
        val exception = assertThrows<Exception> { Email("") }
        exception.message?.contains("Email must not be empty")?.let { assertTrue(it) }
    }

    @Test
    fun `blank email should throw an exception`() {
        val exception = assertThrows<IllegalArgumentException> { Email(" ") }
        exception.message?.contains("Email must not be blank")?.let { assertTrue(it) }
    }

    @Test
    fun `invalid email format should throw an exception`() {
        val exception = assertThrows<IllegalArgumentException> { Email("invalid-doi-format") }
        exception.message?.contains("Wrong Email format")?.let { assertTrue(it) }
    }

    @Test
    fun `should not accept email with no subdomain domain`() {
        val exception = assertThrows<IllegalArgumentException> { Email("email@.com") }
        exception.message?.contains("Wrong Email format")?.let { assertTrue(it) }
    }

    @Test
    fun `should not accept email with a dot and at sign in sequence`() {
        val exception = assertThrows<IllegalArgumentException> { Email("email.@domain.com") }
        exception.message?.contains("Wrong Email format")?.let { assertTrue(it) }
    }


    @Test
    fun `should not accept email with two equal TLDs`() {
        val exception = assertThrows<IllegalArgumentException> { Email("email@ifsp.com.com") }
        exception.message?.contains("Wrong Email format")?.let { assertTrue(it) }
    }

    @Test
    fun `should not accept email with two equal subdomains`() {
        val exception = assertThrows<IllegalArgumentException> { Email("email@ifsp.ifsp.com") }
        exception.message?.contains("Wrong Email format")?.let { assertTrue(it) }
    }

    @Test
    fun `email with incorrect format should include error message`() {
        val exception = assertThrows<IllegalArgumentException> { Email("invalid-email-format") }
        exception.message?.contains("Wrong Email format")?.let { assertTrue(it) }
    }

    @Test
    fun `email without domain should throw an exception`() {
        val exception = assertThrows<IllegalArgumentException> { Email("user@") }
        exception.message?.contains("Wrong Email format")?.let { assertTrue(it) }
    }

    @Test
    fun `invalid email without username should throw an exception`() {
        val exception = assertThrows<IllegalArgumentException> { Email("@domain.com") }
        exception.message?.contains("Wrong Email format")?.let { assertTrue(it) }
    }

    @Test
    fun `invalid email with spaces should throw an exception`() {
        val exception1 = assertThrows<IllegalArgumentException> { Email("user name@example.com") }
        exception1.message?.contains("Wrong Email format")?.let { assertTrue(it) }
        val exception2 = assertThrows<IllegalArgumentException> { Email("   username@example.com") }
        exception2.message?.contains("Wrong Email format")?.let { assertTrue(it) }
        val exception3 = assertThrows<IllegalArgumentException> { Email("username@example.com   ") }
        exception3.message?.contains("Wrong Email format")?.let { assertTrue(it) }
    }

    @Test
    fun `invalid email with special characters should throw an exception`() {
        val exception = assertThrows<IllegalArgumentException> { Email("user!name@example.com") }
        exception.message?.contains("Wrong Email format")?.let { assertTrue(it) }
    }

    @Test
    fun `invalid email with consecutive dots in the domain should throw an exception`() {
        val exception = assertThrows<IllegalArgumentException> { Email("user@domain..com") }
        exception.message?.contains("Wrong Email format")?.let { assertTrue(it) }
    }

    @Test
    fun `invalid email with consecutive dots in the local part should throw an exception`() {
        val exception = assertThrows<IllegalArgumentException> { Email("user..email@domain.com") }
        exception.message?.contains("Wrong Email format")?.let { assertTrue(it) }
    }

    @Test
    fun `invalid email with invalid characters in the domain should throw an exception`() {
        val exception = assertThrows<IllegalArgumentException> { Email("user@doma!n.com") }
        exception.message?.contains("Wrong Email format")?.let { assertTrue(it) }
    }

    @Test
    fun `invalid email with square brackets in the domain should throw an exception`() {
        val exception = assertThrows<IllegalArgumentException> { Email("user@[192.168.1.1]") }
        exception.message?.contains("Wrong Email format")?.let { assertTrue(it) }
    }

    @Test
    fun `invalid email with excessive local-part length should throw an exception`() {
        val exception = assertThrows<IllegalArgumentException> { Email("username_that_exceeds_the_maximum_allowed_length_of_64_characters@example.com") }
        exception.message?.contains("Wrong Email format")?.let { assertTrue(it) }
    }

    @Test
    fun `valid email with local-part under maximum length should not throw an exception`() {
        assertDoesNotThrow { Email("username_that_not_exceeds_the_max_allowed_character_length_of_64@example.com") }
    }

    @Test
    fun `invalid email with excessive domain length should throw an exception`() {
        val exception = assertThrows<IllegalArgumentException> { Email("user@" + "a".repeat(252) + ".com") }
        exception.message?.contains("Wrong Email format")?.let { assertTrue(it) }
    }

    @Test
    fun `valid email with domain under maximum length should not throw an exception`() {
        assertDoesNotThrow { Email("user@" + "a".repeat(251) + ".com") }
    }

    @Test
    fun `invalid email with multiple at sign symbols should throw an exception`() {
        val exception = assertThrows<IllegalArgumentException> { Email("user@domain@com") }
        exception.message?.contains("Wrong Email format")?.let { assertTrue(it) }
    }


}
