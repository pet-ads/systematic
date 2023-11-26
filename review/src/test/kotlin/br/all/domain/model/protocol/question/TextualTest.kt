package br.all.domain.model.protocol.question

import br.all.domain.model.protocol.ProtocolId
import br.all.domain.model.question.QuestionId
import br.all.domain.model.question.Textual
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class TextualTest {

    @Test
    fun `should validate non-blank answer`() {
        val sut = Textual(
            QuestionId(UUID.randomUUID()),
            ProtocolId(UUID.randomUUID()),
            "T1",
            "SAMPLE",
        )
        assertDoesNotThrow{ sut.answer = "abc" }
    }

    @Test
    fun `should throw NullPointerException for a null answer`() {
        val sut = Textual(
            QuestionId(UUID.randomUUID()),
            ProtocolId(UUID.randomUUID()),
            "T1",
            "SAMPLE",
        )

        assertThrows<NullPointerException>{sut.answer = null}
    }

    @Test
    fun `should throw IllegalArgumentException for blank answer`() {
        val sut = Textual(
            QuestionId(UUID.randomUUID()),
            ProtocolId(UUID.randomUUID()),
            "T1",
            "SAMPLE",
        )

        assertThrows<IllegalArgumentException>{sut.answer = "  "}
    }
}