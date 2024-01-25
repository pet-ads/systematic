package br.all.domain.model.question

import br.all.domain.model.protocol.ProtocolId
import br.all.domain.model.study.Answer
import io.github.serpro69.kfaker.Faker
import org.junit.jupiter.api.*
import java.util.*
import kotlin.test.assertEquals

@Tag("UnitTest")
class LabeledScaleTest {
    private val faker = Faker()

    private val validScale = mapOf(faker.lorem.words() to 1, faker.lorem.words() to 2)
    private val emptyScale = emptyMap<String, Int>()

    @Nested
    @Tag("ValidClasses")
    @DisplayName("When successfully creating LabeledScale questions")
    inner class WhenSuccessfullyCreatingLabeledScaleQuestions {
        @Test
        fun `should create a LabeledScale question with valid parameters`() {
            val id = QuestionId(UUID.randomUUID())
            val protocolId = ProtocolId(UUID.randomUUID())
            val code = faker.lorem.words()
            val description = faker.lorem.words()

            assertDoesNotThrow { LabeledScale(id, protocolId, code, description, validScale) }
        }

        @Test
        fun `should answer the questions with a valid scale`() {
            val label = Label("Label", 3)
            val scales = mutableMapOf(label.name to label.value).also { it.putAll(validScale) }
            val id = QuestionId(UUID.randomUUID())
            val question = createLabeledScale(id, scales)
            val expectedAnswer = Answer(id.value, label)

            assertEquals(expectedAnswer, question.answer(label))
        }

        @Test
        fun `should a label that is not yet in the scales be added to it`() {
            val scales = mutableMapOf("Label" to 1)
            val id = QuestionId(UUID.randomUUID())
            val question = createLabeledScale(id, scales)

            question.addScale("Other label", 2)

            assertEquals(2, question.scales.size)
        }
    }

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("When unable to create LabeledScale questions")
    inner class WhenUnableToCreateLabeledScaleQuestions {
        @Test
        fun `should throw IllegalArgumentException for empty scales`() {
            val id = QuestionId(UUID.randomUUID())
            val protocolId = ProtocolId(UUID.randomUUID())
            val code = faker.lorem.words()
            val description = faker.lorem.words()

            assertThrows<IllegalArgumentException> { LabeledScale(id, protocolId, code, description, emptyScale) }
        }

        @Test
        fun `should not answer a question if it does not have the label`() {
            val scales = mutableMapOf("Label" to 1)
            val id = QuestionId(UUID.randomUUID())
            val question = createLabeledScale(id, scales)

            assertThrows<IllegalArgumentException> { question.answer(Label("Other Label", 3)) }
        }
    }

    private fun createLabeledScale(
        id: QuestionId,
        scales: MutableMap<String, Int>
    ): LabeledScale {
        val protocolId = ProtocolId(UUID.randomUUID())
        val question = LabeledScale(id, protocolId, faker.lorem.words(), faker.lorem.words(), scales)
        return question
    }
}