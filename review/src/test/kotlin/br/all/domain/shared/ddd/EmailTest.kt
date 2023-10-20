package br.all.domain.shared.ddd

import br.all.domain.shared.utils.Email
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class EmailTest {

    @Test
    fun `valid EMAIL should not throw an exception`() {
        val emailValue = "test@email.com"
        val email = Email(emailValue)

        //TODO what are you testing here?
    }

    @Test
    fun `equal emails should be equal`() {
        val emailValue = "test1@email.com"

        val email1 = Email(emailValue)
        val email2 = Email(emailValue)

        assertEquals(email1, email2)
    }

    @Test
    fun `different emails should not be equal`() {
        val emailValue1 = "test1@email.com" // TODO in some cases, just keep it simple
        val emailValue2 = "test2@email.com"

        val email1 = Email("test1@email.com")
        val email2 = Email("test2@email.com")

        assertNotEquals(email1, email2)
    }

    @Test
    fun `empty email should throw an exception`() {
        assertThrows<IllegalArgumentException> { Email("") }
    }

    @Test
    fun `blank email should throw an exception`() {
        assertThrows<IllegalArgumentException> { Email(" ") }
    }

    @Test
    fun `invalid email format should throw an exception`() {
        assertThrows<IllegalArgumentException> { Email("invalid-doi-format") }
    }

    @Disabled
    @Test
    fun `should not accept email with no subdomain domain`() {
        assertThrows<IllegalArgumentException> { Email("email@.com") }
    }

    @Disabled
    @Test
    fun `should not accept email with two equal TLDs`() {
        assertThrows<IllegalArgumentException> { Email("email@ifsp.com.com") }
    }

    //TODO Google of GPT for any possible format of invalid e-mail and create a test for it

    @Test
    fun `email with incorrect format should include error message`() {
        val emailValue = "invalid-email-format"
        val exception = assertThrows<IllegalArgumentException> {
            Email(emailValue)
        }
        assert(exception.message?.contains("Wrong Email format") == true)
    }
}
