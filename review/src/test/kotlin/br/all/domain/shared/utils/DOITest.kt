package br.all.domain.shared.utils


import br.all.domain.shared.valueobject.DOI
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class DOITest {
    @Test
    fun `valid DOI should not throw an exception`() {
        assertDoesNotThrow { DOI("10.1590/1089-6891v16i428131") }
    }

    @Test
    fun `equal DOIs should be equal`() {
        val doi1 = DOI("10.1234/abcd5678")
        val doi2 = DOI("10.1234/abcd5678")
        assertEquals(doi1, doi2)
    }

    @Test
    fun `different DOIs should not be equal`() {
        val doi1 = DOI("10.1234/abcd5678")
        val doi2 = DOI("10.5678/efgh1234")
        assertNotEquals(doi1, doi2)
    }

    @Test
    fun `empty DOI should throw an exception`() {
        val exception = assertThrows<IllegalArgumentException> { DOI("") }
        assertTrue(exception.message?.contains("DOI must not be empty") ?: false)
    }

    @Test
    fun `blank DOI should throw an exception`() {
        val exception = assertThrows<IllegalArgumentException> { DOI(" ") }
        assertTrue(exception.message?.contains("DOI must not be blank") ?: false)
    }

    @Test
    fun `invalid DOI format should throw an exception`() {
        val exception = assertThrows<IllegalArgumentException> { DOI("invalid-doi-format") }
        assertTrue(exception.message?.contains("Wrong DOI format") ?: false)
    }

    @Test
    fun `DOI not started with '10' should throw an exception`() {
        val exception = assertThrows<IllegalArgumentException> { DOI("11.1590/1089-6891v16i428131") }
        assertTrue(exception.message?.contains("Wrong DOI format") ?: false)
    }

    @Test
    fun `ainvalid DOI format should throw an exception`() {
        val exception = assertThrows<IllegalArgumentException> { DOI("10.1a590/1089-6891v16i428131") }
        assertTrue(exception.message?.contains("Wrong DOI format") ?: false)
    }

    @Test
    fun `invalid DOI with unauthorized symbols should throw an exception`() {
        val exception = assertThrows<IllegalArgumentException> { DOI("10.1590/10!@#9-6891v16i428131") }
        assertTrue(exception.message?.contains("Wrong DOI format") ?: false)
    }

    @Test
    fun `invalid DOI with double slashes should throw an exception`() {
        val exception = assertThrows<IllegalArgumentException> { DOI("10.1590//109-6891v16i428131") }
        assertTrue(exception.message?.contains("Wrong DOI format") ?: false)
    }

    @Test
    fun `invalid DOI with two slashes should throw an exception`() {
        val exception = assertThrows<IllegalArgumentException> { DOI("10.1590/2344342/108131") }
        assertTrue(exception.message?.contains("Wrong DOI format") ?: false)
    }

    @Test
    fun `invalid DOI with double dots should throw an exception`() {
        val exception = assertThrows<IllegalArgumentException> { DOI("10..1590/2344342/108131") }
        assertTrue(exception.message?.contains("Wrong DOI format") ?: false)
    }

    @Test
    fun `invalid DOI  with two dots should throw an exception`() {
        val exception = assertThrows<IllegalArgumentException> { DOI("10.15.90/2344342/108131") }
        assertTrue(exception.message?.contains("Wrong DOI format") ?: false)
    }

}