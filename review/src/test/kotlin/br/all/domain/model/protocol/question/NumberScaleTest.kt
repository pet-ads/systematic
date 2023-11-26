package br.all.domain.model.protocol.question

import br.all.domain.model.protocol.ProtocolId
import br.all.domain.model.question.NumberScale
import br.all.domain.model.question.QuestionId
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class NumberScaleTest {
    @Test
    fun `should validate non null and inside bounds answer`() {
        val sut = NumberScale(
            QuestionId(UUID.randomUUID()),
            ProtocolId(UUID.randomUUID()),
            "T1",
            "SAMPLE",
            10,
            0
        )

        assertDoesNotThrow{ sut.answer = 3 }
    }

    @Test
    fun `should throw NullPointerException for null a answer`() {
        val sut = NumberScale(
            QuestionId(UUID.randomUUID()),
            ProtocolId(UUID.randomUUID()),
            "T1",
            "SAMPLE",
            10,
            0
        )

        assertThrows<NullPointerException> { sut.answer = null }
    }

    @Test
    fun `should throw IllegalArgumentException for answer above upper bounds`() {
        val sut = NumberScale(
            QuestionId(UUID.randomUUID()),
            ProtocolId(UUID.randomUUID()),
            "T1",
            "SAMPLE",
            10,
            0
        )

        assertThrows<IllegalArgumentException>{sut.answer = 11}
    }

    @Test
    fun `should throw IllegalArgumentException for answer below lower bounds`() {
        val sut = NumberScale(
            QuestionId(UUID.randomUUID()),
            ProtocolId(UUID.randomUUID()),
            "T1",
            "SAMPLE",
            10,
            0
        )

        assertThrows<IllegalArgumentException>{sut.answer = -1}
    }
}