package br.all.domain.shared.utils.phrase

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class PhraseTest {
    @Test
    fun `Should accept words with only lowercase letters`() {
        assertDoesNotThrow { Phrase("phrase with only lower case words") }
    }

    @Test
    fun `Should accept words with only uppercase letters`() {
        assertDoesNotThrow { Phrase("PHRASE WITH WORDS OF ONLY UPPERCASE LETTERS") }
    }

    @Test
    fun `Should accept words with uppercase and lowercase letters`() {
        assertDoesNotThrow { Phrase("Green gReen gREEN GrEEN") }
    }

    @Test
    fun `Should accept digits and not punctuation symbols when they are not within not quoted words`() {
        assertDoesNotThrow { Phrase("7 not quoted & 2023 #3&") }
    }

    @Test
    fun `Should throw if there are symbols within not quoted words`() {
        val invalidPhrase = "normal words \"quoted words\" @word w@rd h&"
        val exception = assertThrows<IllegalArgumentException> {
            Phrase(invalidPhrase)
        }
        assertEquals("Symbols should not be within not quoted words in a phrase. " +
                "Provided: $invalidPhrase", exception.message)
    }

    @Test
    fun `Should digits be within not quoted words`() {
        assertDoesNotThrow { Phrase("CO2 H2O 1x e4") }
    }

    @Test
    fun `Should allow punctuation signs to follow words`() {
        assertDoesNotThrow { Phrase("word, word; word. word: word) word] word} word? word!") }
    }
    
    @Test
    fun `Should hyphens and apostrophes be able to be within words`() {
        assertDoesNotThrow { Phrase("word-more-word word's") }
    }

    @Test
    fun `Should accept digits and symbols within words if it is quoted`() {
        assertDoesNotThrow { Phrase("\"quoted.word$8946532s\"") }
    }

    @Test
    fun `Should throw with useful message when description is blank`() {
        val exception = assertThrows<IllegalArgumentException> { Phrase("") }
        assertEquals("A phrase must not be blank!", exception.message)
    }
}
