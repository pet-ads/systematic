package br.all.domain.model.protocol.question

import br.all.domain.model.protocol.ProtocolId
import br.all.domain.model.question.Label
import br.all.domain.model.question.LabeledScale
import br.all.domain.model.question.QuestionId
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*
import kotlin.NoSuchElementException

class LabeledScaleTest {

    //TODO em resumo, para tudo que é público e não gerado automaticamente, teste:
    // uma entrada válida, todas as possíveis entradas inválidas, uma por teste. Além disso, teste listas vazias, listas
    // com um único elemento, limites iguais (higher,lower), limites quase iguais (lower == higher -1), limites inválidos.
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
        assertDoesNotThrow { labeledScale.answer = answer }
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
        assertThrows<NoSuchElementException> { labeledScale.answer = answer }
    }

    @Test
    fun `should throw IllegalArgumentException for empty scales`() {
        val questionId = QuestionId(UUID.randomUUID())
        val protocolId = ProtocolId(UUID.randomUUID())
        val code = "T1"
        val description = "Sample labeled scale question"
        val scales = emptyMap<String, Int>()
        assertThrows<IllegalArgumentException> { LabeledScale(questionId, protocolId, code, description, scales) }

    }

}