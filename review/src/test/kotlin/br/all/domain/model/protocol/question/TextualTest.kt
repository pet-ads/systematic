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

        //TODO validade é um interesse interno, não público. Teste o efeito acoplado da classe. Ou seja, tente criar
        // e veja se ela se comporta como quer (dar certo ou dar erro)
        //val result = textualQuestion.validateAnswer(answer)

        //assertEquals(answer, result)
    }

    @Test
    fun `should throw NullPointerException for a null answer`() {
        val questionId = QuestionId(UUID.randomUUID())
        val protocolId = ProtocolId(UUID.randomUUID())
        val code = "T1"
        val description = "Sample textual question"
        val textualQuestion = Textual(questionId, protocolId, code, description)

        //TODO em kotlin, quando você chama .answer você não se refere a variável, mas property (setter e/ou getter)
        //assertThrows(NullPointerException::class.java) { textualQuestion.validateAnswer(null) }
        assertThrows(NullPointerException::class.java) { textualQuestion.answer = null }
    }

    @Test
    fun `should throw IllegalArgumentException for blank answer`() {
        val questionId = QuestionId(UUID.randomUUID())
        val protocolId = ProtocolId(UUID.randomUUID())
        val code = "T1"
        val description = "Sample textual question"
        val textualQuestion = Textual(questionId, protocolId, code, description)
        val answer = ""
        //todo idem aqui. Além disso, não precisa criar campo se o texto ele puder ser usado direto no construtor
        //assertThrows(IllegalArgumentException::class.java) { textualQuestion.validateAnswer(answer) }
    }
}