package br.all.domain.shared.ddd

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class EmailTest {

    @Test
    fun `valid EMAIL should not throw an exception`() {
        val emailValue = "test@email.com"
        val email = Email(emailValue)
    }

    @Test
    fun `equal EMAILs should be equal`() {
        val emailValue = "test1@email.com"

        val email1 = Email(emailValue)
        val email2 = Email(emailValue)

        assertEquals(email1, email2)
    }

    @Test
    fun `diferent EMAILs should not be equal`() {
        val emailValue1 = "test1@email.com"
        val emailValue2 = "test2@email.com"

        val email1 = Email(emailValue1)
        val email2 = Email(emailValue2)

        assertNotEquals(email1, email2)
    }

    @Test
    fun `empty EMAIL should throw an exception`() {
        val emailValue = ""
        assertThrows<IllegalArgumentException> {
            Email(emailValue)
        }
    }

    @Test
    fun `blank EMAIL should throw an exception`() {
        val emailValue = " "
        assertThrows<IllegalArgumentException> {
            Email(emailValue)
        }
    }

    @Test
    fun `invalid EMAIL format should throw an exception`() {
        val emailValue = "invalid-doi-format"
        assertThrows<IllegalArgumentException> {
            Email(emailValue)
        }
    }

    @Test
    fun `EMAIL with incorrect format should include error message`() {
        val emailValue = "invalid-email-format"
        val exception = assertThrows<IllegalArgumentException> {
            Email(emailValue)
        }
        assert(exception.message?.contains("Wrong Email format") == true)
    }
}
