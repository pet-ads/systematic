package br.all.domain.model.protocol.question

import br.all.domain.model.protocol.ProtocolId
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

class PickListTest {
    @Test
    fun `should validate non blank answer`() {
        val questionId = QuestionId(UUID.randomUUID())
        val protocolId = ProtocolId(UUID.randomUUID())
        val code = "T1"
        val description = "Sample picklist question"
        val labelList: List<String> = listOf("a", "b", "c")
        val pickList = PickList(questionId, protocolId, code, description, labelList)

        val answer = "a"
        val result = pickList.validateAnswer(answer)
        assertEquals(answer, result)
    }

    @Test
    fun `should throw IllegalArgumentException for blank answer`() {
        val questionId = QuestionId(UUID.randomUUID())
        val protocolId = ProtocolId(UUID.randomUUID())
        val code = "T1"
        val description = "Sample picklist question"
        val labelList: List<String> = listOf("a", "b", "c")
        val pickList = PickList(questionId, protocolId, code, description, labelList)

        val answer = ""
        assertThrows(IllegalArgumentException::class.java) { pickList.validateAnswer(answer) }
    }

    @Test
    fun `should throw NullPointerException for null answer`() {
        val questionId = QuestionId(UUID.randomUUID())
        val protocolId = ProtocolId(UUID.randomUUID())
        val code = "T1"
        val description = "Sample picklist question"
        val labelList: List<String> = listOf("a", "b", "c")
        val pickList = PickList(questionId, protocolId, code, description, labelList)

        val answer = null
        assertThrows(NullPointerException::class.java) { pickList.validateAnswer(answer) }
    }

    @Test
    fun `should throw IllegalArgumentException for answer not in options`() {
        val questionId = QuestionId(UUID.randomUUID())
        val protocolId = ProtocolId(UUID.randomUUID())
        val code = "T1"
        val description = "Sample picklist question"
        val labelList: List<String> = listOf("a", "b", "c")
        val pickList = PickList(questionId, protocolId, code, description, labelList)

        val answer = "d"
        assertThrows(IllegalArgumentException::class.java) { pickList.validateAnswer(answer) }
    }
}