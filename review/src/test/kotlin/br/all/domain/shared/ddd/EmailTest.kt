package br.all.domain.shared.ddd

import br.all.domain.shared.utils.Email
import br.all.domain.shared.utils.Text
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class EmailTest {

    @Test
    fun `valid EMAIL should not throw an exception`() {
        assertDoesNotThrow { Email("email@email.com") }
    }

    @Test
    fun `equal emails should be equal`() {
        val email1 = Email("email@email.com")
        val email2 = Email("email@email.com")
        assertEquals(email1, email2)
    }

    @Test
    fun `different emails should not be equal`() {
        val email1 = Email("email@email.com")
        val email2 = Email("another_email@email.com")
        assertNotEquals(email1, email2)
    }

    @Test
    fun `empty email should throw an exception`() {
        val exception = assertThrows<IllegalArgumentException> { Email("") }
        exception.message?.contains("Wrong Email format")?.let { assertTrue(it) }
    }

    @Test
    fun `blank email should throw an exception`() {
        val exception = assertThrows<IllegalArgumentException> { Email(" ") }
        exception.message?.contains("Wrong Email format")?.let { assertTrue(it) }
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
    fun `invalid email with excessive length should throw an exception`() {
        val exception = assertThrows<IllegalArgumentException> { Email("a_very_long_username_that_exceeds_the_maximum_allowed_length@example.com") }
        exception.message?.contains("Wrong Email format")?.let { assertTrue(it) }
    }

    @Test
    fun `invalid email with multiple at sign symbols should throw an exception`() {
        val exception = assertThrows<IllegalArgumentException> { Email("user@domain@com") }
        exception.message?.contains("Wrong Email format")?.let { assertTrue(it) }
    }


}
