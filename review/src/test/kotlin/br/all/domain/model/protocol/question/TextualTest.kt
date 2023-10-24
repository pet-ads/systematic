package br.all.domain.model.protocol.question

import br.all.domain.model.protocol.ProtocolId
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

class TextualTest {

    @Test
    fun `should validate non-blank answer`() {
        val questionId = QuestionId(UUID.randomUUID())
        val protocolId = ProtocolId(UUID.randomUUID())

        val code = "T1"
        val description = "Sample textual question"
        val textualQuestion = Textual(questionId, protocolId, code, description)

        val answer = "Valid Answer"
        val result = textualQuestion.validateAnswer(answer)

        assertEquals(answer, result)
    }

    @Test
    fun `should throw NullPointerException for null a answer`() {
        val questionId = QuestionId(UUID.randomUUID())
        val protocolId = ProtocolId(UUID.randomUUID())
        val code = "T1"
        val description = "Sample textual question"
        val textualQuestion = Textual(questionId, protocolId, code, description)
        assertThrows(NullPointerException::class.java) { textualQuestion.validateAnswer(null) }
    }

    @Test
    fun `should throw IllegalArgumentException for blank answer`() {
        val questionId = QuestionId(UUID.randomUUID())
        val protocolId = ProtocolId(UUID.randomUUID())
        val code = "T1"
        val description = "Sample textual question"
        val textualQuestion = Textual(questionId, protocolId, code, description)
        val answer = ""
        assertThrows(IllegalArgumentException::class.java) { textualQuestion.validateAnswer(answer) }
    }
}