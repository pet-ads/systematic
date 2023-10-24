package br.all.domain.model.protocol.question

import br.all.domain.model.protocol.ProtocolId
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

class NumberScaleTest {
    @Test
    fun `should validate non null and inside bounds answer`() {
        val questionId = QuestionId(UUID.randomUUID())
        val protocolId = ProtocolId(UUID.randomUUID())
        val code = "T1"
        val description = "Sample labeled scale question"
        val higher = 10
        val lower = 1
        val numberScale = NumberScale(questionId, protocolId, code, description, higher, lower)

        val answer = 1
        val result = numberScale.validateAnswer(answer)
        assertEquals(answer, result)
    }

    @Test
    fun `should throw NullPointerException for null a answer`() {
        val questionId = QuestionId(UUID.randomUUID())
        val protocolId = ProtocolId(UUID.randomUUID())
        val code = "T1"
        val description = "Sample labeled scale question"
        val higher = 10
        val lower = 1
        val numberScale = NumberScale(questionId, protocolId, code, description, higher, lower)

        val answer = null
        assertThrows(NullPointerException::class.java) { numberScale.validateAnswer(answer) }
    }

    @Test
    fun `should throw IllegalArgumentException for answer above upper bounds`() {
        val questionId = QuestionId(UUID.randomUUID())
        val protocolId = ProtocolId(UUID.randomUUID())
        val code = "T1"
        val description = "Sample labeled scale question"
        val higher = 10
        val lower = 1
        val numberScale = NumberScale(questionId, protocolId, code, description, higher, lower)

        val answer = 11
        assertThrows(IllegalArgumentException::class.java) { numberScale.validateAnswer(answer) }
    }

    @Test
    fun `should throw IllegalArgumentException for answer below lower bounds`() {
        val questionId = QuestionId(UUID.randomUUID())
        val protocolId = ProtocolId(UUID.randomUUID())
        val code = "T1"
        val description = "Sample labeled scale question"
        val higher = 10
        val lower = 1
        val numberScale = NumberScale(questionId, protocolId, code, description, higher, lower)

        val answer = 0
        assertThrows(IllegalArgumentException::class.java) { numberScale.validateAnswer(answer) }
    }
}