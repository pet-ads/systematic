package br.all.domain.shared.ddd

import br.all.domain.model.protocol.QuestionId
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class QuestionIdTest {

    @Test
    fun `valid QuestionId`() {
        val questionId = QuestionId(1)
        assertEquals(1, questionId.id)
    }

    @Test
    fun `invalid QuestionId - ID equal to 0`() {
        assertFailsWith<IllegalArgumentException> {
            QuestionId(0)
        }
    }

    @Test
    fun `invalid QuestionId - ID less than to 0`() {
        assertFailsWith<IllegalArgumentException> {
            QuestionId(-2)
        }
    }

    @Test
    fun `valid QuestionId - ID is MAX_VALUE`() {
        val questionId = QuestionId(Int.MAX_VALUE)
        assertEquals(Int.MAX_VALUE, questionId.id)
    }

    @Test
    fun `invalid QuestionId - ID is MAX_VALUE`() {
        assertFailsWith<IllegalArgumentException> {
            QuestionId(Int.MIN_VALUE)
        }
    }
}