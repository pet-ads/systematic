package br.all.domain.model.protocol

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

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
    fun `Should accept digits and not punctuation symbols when they are not within not quoted words`() {
        assertDoesNotThrow { Criteria("7 not quoted & 2023 #3&", Criteria.CriteriaType.INCLUSION) }
    }
}