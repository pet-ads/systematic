package br.all.domain.model.protocol.question

import br.all.domain.model.protocol.ProtocolId
import br.all.domain.model.question.PickList
import br.all.domain.model.question.QuestionId
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.assertThrows
import java.util.*
import kotlin.test.Test

class PickListTest {
    @Test
    fun `should throw if receive blank answer`() {
//        val questionId = QuestionId(UUID.randomUUID())
//        val protocolId = ProtocolId(UUID.randomUUID())
//        val code = "T1"
//        val description = "Sample picklist question"
//        val labelList: List<String> = listOf("a", "b", "c")
//        val pickList = PickList(questionId, protocolId, code, description, labelList)
//
//        val answer = "a"
//        val result = pickList.validateAnswer(answer)
//        assertEquals(answer, result)
    }

    @Test
    fun `should throw IllegalArgumentException for blank answer`() {
        val sut = PickList(
            QuestionId(UUID.randomUUID()),
            ProtocolId(UUID.randomUUID()),
            "T1",
            "SAMPLE",
            listOf("a", "b", "c")
        )
        assertThrows<IllegalArgumentException> { sut.answer = " " }
    }

    @Test
    fun `should throw NullPointerException for null answer`() {

    }

    @Test
    fun `should throw IllegalArgumentException for answer not in options`() {

    }
}