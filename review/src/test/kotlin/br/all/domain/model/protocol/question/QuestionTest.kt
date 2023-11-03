package br.all.domain.model.protocol.question

import br.all.domain.model.protocol.ProtocolId
import br.all.domain.shared.ddd.Entity
import br.all.domain.shared.ddd.EntityTest
import br.all.domain.shared.ddd.Notification
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.util.*

class QuestionTest {

    //TODO não entendi porque está fazendo isso. Aqui você está mudando o comportamento do que está testando.
    //Não é uma boa ideia.
    private class QuestionA(id: QuestionId, protocolId: ProtocolId, code: String, description: String) :
        Question<Int>(id, protocolId, code, description) {
        override fun validateAnswer(value: Int?): Int {
            return 0
        }

        init {
            val notification = validate()
            require(notification.hasNoErrors())
        }
    }

    @Test
    fun `should throw IllegalArgumentException for blank val code`() {
        val questionId = QuestionId(UUID.randomUUID())
        val protocolId = ProtocolId(UUID.randomUUID())
        val code = ""
        val description = "Sample question"
        assertThrows(IllegalArgumentException::class.java) { QuestionA(questionId, protocolId, code, description) }
    }

    @Test
    fun `should throw IllegalArgumentException for blank val description`() {
        val questionId = QuestionId(UUID.randomUUID())
        val protocolId = ProtocolId(UUID.randomUUID())
        val code = "a"
        val description = ""
        assertThrows(IllegalArgumentException::class.java) { QuestionA(questionId, protocolId, code, description) }
    }

    @Test
    fun `should throw IllegalArgumentException for blank both val description and code`() {
        val questionId = QuestionId(UUID.randomUUID())
        val protocolId = ProtocolId(UUID.randomUUID())
        val code = ""
        val description = ""
        assertThrows(IllegalArgumentException::class.java) { QuestionA(questionId, protocolId, code, description) }
    }
}
