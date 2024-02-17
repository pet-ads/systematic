package br.all.domain.model.question

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
    private lateinit var validLabeledScale: LabeledScale

    private val validScale = mapOf("label1" to 1, "label2" to 2)
    private val emptyScale = emptyMap<String, Int>()

    @BeforeEach
    fun setUp() {
        validLabeledScale = LabeledScale(
            QuestionId(UUID.randomUUID()),
            SystematicStudyId(UUID.randomUUID()),
            faker.lorem.words(),
            faker.lorem.words(),
            validScale
        )
    }

    @Nested
    @Tag("ValidClasses")
    @DisplayName("WhenSuccessfully")
    inner class WhenSuccessfully {
        @Test
        fun `should answer the questions with a valid scale`() {
            val label = Label("label1", 1)
            val expectedAnswer = Answer(validLabeledScale.id.value(), label)

            assertEquals(expectedAnswer, validLabeledScale.answer(label))
        }

        @Test
        fun `should a label that is not yet in the scales be added to it`() {
            validLabeledScale.addScale("label3", 3)

            assertEquals(3, validLabeledScale.scales.size)
        }

        @Test
        fun `should a existing label be removed if not the last one in the scales`() {
            validLabeledScale.removeScale("label2")

            assertEquals(1, validLabeledScale.scales.size)
        }
    }

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("WhenUnable")
    inner class WhenUnable {
        @Test
        fun `should throw IllegalArgumentException for empty scales`() {
            assertThrows<IllegalArgumentException> {
                LabeledScale(
                    QuestionId(UUID.randomUUID()),
                    SystematicStudyId(UUID.randomUUID()),
                    faker.lorem.words(),
                    faker.lorem.words(),
                    emptyScale
                )
            }
        }

        @Test
        fun `should not answer a question if it does not have the label`() {
            assertThrows<IllegalArgumentException> { validLabeledScale.answer(Label("label3", 3)) }
        }

        @Test
        fun `should not remove a Label from scale if it is the last one remaining`() {
            validLabeledScale.removeScale("label2")

            assertThrows<IllegalStateException> { validLabeledScale.removeScale("label1") }
        }

        @Test
        fun `should throw IllegalArgumentException for non-existing Label`() {
            assertThrows<NoSuchElementException> { validLabeledScale.removeScale("label3") }
        }
    }
}