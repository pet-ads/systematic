package br.all.domain.model.question

import br.all.domain.model.protocol.ProtocolId
import br.all.domain.model.review.SystematicStudyId
import br.all.domain.model.study.Answer
import io.github.serpro69.kfaker.Faker
import org.junit.jupiter.api.*
import java.lang.IllegalStateException
import java.util.*
import kotlin.NoSuchElementException
import kotlin.test.assertEquals

@Tag("UnitTest")
class LabeledScaleTest {
    private val faker = Faker()

    private val validScale = mapOf(faker.lorem.words() to 1, faker.lorem.words() to 2)
    private val emptyScale = emptyMap<String, Int>()

    @Nested
    @Tag("ValidClasses")
    @DisplayName("WhenSuccessfully")
    inner class WhenSuccessfully {
        @Test
        fun `should create a LabeledScale question with valid parameters`() {
            val id = QuestionId(UUID.randomUUID())
            val systematicStudyId = SystematicStudyId(UUID.randomUUID())
            val code = faker.lorem.words()
            val description = faker.lorem.words()

            assertDoesNotThrow { LabeledScale(id, systematicStudyId, code, description, validScale) }
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

        @Test
        fun `should a existing label be removed if not the last one in the scales`() {
            val scales = mutableMapOf("Label" to 1, "Label2" to 2)
            val id = QuestionId(UUID.randomUUID())
            val question = createLabeledScale(id, scales)

            question.removeScale("Label2")

            assertEquals(1, question.scales.size)
        }
    }

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("WhenUnable")
    inner class WhenUnable {
        @Test
        fun `should throw IllegalArgumentException for empty scales`() {
            val id = QuestionId(UUID.randomUUID())
            val systematicStudyId = SystematicStudyId(UUID.randomUUID())
            val code = faker.lorem.words()
            val description = faker.lorem.words()

            assertThrows<IllegalArgumentException> { LabeledScale(id, systematicStudyId, code, description, emptyScale) }
        }

        @Test
        fun `should not answer a question if it does not have the label`() {
            val scales = mutableMapOf("Label" to 1)
            val id = QuestionId(UUID.randomUUID())
            val question = createLabeledScale(id, scales)

            assertThrows<IllegalArgumentException> { question.answer(Label("Other Label", 3)) }
        }

        @Test
        fun `should not remove a Label from scale if it is the last one remaining`() {
            val scales = mutableMapOf("Label" to 1)
            val id = QuestionId(UUID.randomUUID())
            val question = createLabeledScale(id, scales)

            assertThrows<IllegalStateException> { question.removeScale("Label") }
        }

        @Test
        fun `should throw IllegalArgumentException for non-existing Label`() {
            val scales = mutableMapOf("Label" to 1, "Label2" to 2)
            val id = QuestionId(UUID.randomUUID())
            val question = createLabeledScale(id, scales)

            assertThrows<NoSuchElementException> { question.removeScale("Label3") }
        }
    }

    private fun createLabeledScale(
        id: QuestionId,
        scales: MutableMap<String, Int>
    ): LabeledScale {
        val systematicStudyId = SystematicStudyId(UUID.randomUUID())
        return LabeledScale(id, systematicStudyId, faker.lorem.words(), faker.lorem.words(), scales)
    }
}