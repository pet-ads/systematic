package br.all.domain.model.protocol.question

import br.all.domain.model.protocol.ProtocolId
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*
import kotlin.NoSuchElementException

class LabeledScaleTest {
    @Test
    fun `should validate non blank answer`() {
        val questionId = QuestionId(UUID.randomUUID())
        val protocolId = ProtocolId(UUID.randomUUID())
        val code = "T1"
        val description = "Sample labeled scale question"
        val scales = mapOf(
            "Label1" to 1,
            "Label2" to 2,
            "Label3" to 3
        )
        val labeledScale = LabeledScale(questionId, protocolId, code, description, scales)

        val answer = Label("Label1", 1)
        val result = labeledScale.validateAnswer(answer)
        assertEquals(answer, result)
    }

    @Test
    fun `should validate answer not in scales`() {
        val questionId = QuestionId(UUID.randomUUID())
        val protocolId = ProtocolId(UUID.randomUUID())
        val code = "T1"
        val description = "Sample labeled scale question"
        val scales = mapOf(
            "Label1" to 1,
            "Label2" to 2,
            "Label3" to 3
        )
        val labeledScale = LabeledScale(questionId, protocolId, code, description, scales)

        val answer = Label("InvalidLabel", 4)
        assertThrows(NoSuchElementException::class.java) { labeledScale.validateAnswer(answer) }
    }

    @Test
    fun `should validate empty scales`() {
        val questionId = QuestionId(UUID.randomUUID())
        val protocolId = ProtocolId(UUID.randomUUID())
        val code = "T1"
        val description = "Sample labeled scale question"
        val scales = emptyMap<String, Int>()
        assertThrows<IllegalArgumentException> { LabeledScale(questionId, protocolId, code, description, scales) }

    }

    @Test
    fun `should validate null answer`() {
        val questionId = QuestionId(UUID.randomUUID())
        val protocolId = ProtocolId(UUID.randomUUID())
        val code = "T1"
        val description = "Sample labeled scale question"
        val scales = mapOf(
            "Label1" to 1,
            "Label2" to 2,
            "Label3" to 3
        )
        val labeledScale = LabeledScale(questionId, protocolId, code, description, scales)

        val answer: Label? = null
        assertThrows<NoSuchElementException> {
            labeledScale.validateAnswer(answer)
        }
    }
}