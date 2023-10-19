package br.all.domain.model.protocol

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

class CriteriaTest {
    @Test
    fun `Should accept words with only lowercase letters`() {
        assertDoesNotThrow { Criteria("phrase with only lower case words", Criteria.CriteriaType.INCLUSION) }
    }
}