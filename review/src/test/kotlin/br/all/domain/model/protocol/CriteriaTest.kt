package br.all.domain.model.protocol

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class CriteriaTest {
    @Test
    fun `Should accept words with only lowercase letters`() {
        assertDoesNotThrow { Criteria("phrase with only lower case words", Criteria.CriteriaType.INCLUSION) }
    }

    @Test
    fun `Should accept words with only uppercase letters`() {
        assertDoesNotThrow {
            Criteria("PHRASE WITH WORDS OF ONLY UPPERCASE LETTERS", Criteria.CriteriaType.INCLUSION)
        }
    }

    @Test
    fun `Should accept words with uppercase and lowercase letters`() {
        assertDoesNotThrow { Criteria("Green gReen gREEN GrEEN", Criteria.CriteriaType.INCLUSION) }
    }
    
    @Test
    fun `Should accept digits and not punctuation symbols when they are not within not quoted words`() {
        assertDoesNotThrow { Criteria("7 not quoted & 2023 #3&", Criteria.CriteriaType.INCLUSION) }
    }

    @Test
    fun `Should throw if there are digits and symbols within not quoted words`() {
        val invalidDescription = "1word w0rd f4 @word w@rd h&"
        val exception = assertThrows<IllegalArgumentException> {
            Criteria(invalidDescription, Criteria.CriteriaType.INCLUSION)
        }
        assertEquals("Symbols and numbers should be within not quoted words in criteria description. " +
                "Provided: $invalidDescription", exception.message)
    }

    @Test
    fun `Should accept digits and symbols within words if it is quoted`() {
        assertDoesNotThrow { Criteria("\"quoted.word$8946532s\"", Criteria.CriteriaType.INCLUSION) }
    }
}