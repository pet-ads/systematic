package br.all.domain.model.question

import br.all.domain.model.protocol.ProtocolId
import br.all.domain.model.review.SystematicStudyId
import br.all.domain.model.study.Answer
import io.github.serpro69.kfaker.Faker
import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.util.*
import kotlin.test.assertEquals

@Tag("UnitTest")
class NumberScaleTest {
    private val faker = Faker()
    private lateinit var validNumberScale: NumberScale

    @BeforeEach
    fun setUp() {
        val higher = 10
        val lower = 1
        validNumberScale = NumberScale(
            QuestionId(UUID.randomUUID()),
            SystematicStudyId(UUID.randomUUID()),
            faker.lorem.words(),
            faker.lorem.words(),
            higher,
            lower
        )
    }

    @Nested
    @Tag("ValidClasses")
    @DisplayName("When successfully answering NumberScale questions")
    inner class WhenSuccessfullyAnsweringNumberScaleQuestions {

        @ParameterizedTest(name = "[{index}]: value = \"{0}\"")
        @ValueSource(ints = [1, 10, 5])
        fun `should answer the questions with valid value equal or between bounds`(value: Int) {
            val expectedAnswer = Answer(validNumberScale.id.value(), value)

            assertEquals(expectedAnswer, validNumberScale.answer(value))
        }
    }

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("When being unable to create NumberScale questions")
    inner class WhenBeingUnableToCreateNumberScaleQuestions {
        @Test
        fun `should throw IllegalArgumentException for equal higher and lower values`() {
            assertThrows<IllegalArgumentException> {
                NumberScale(
                    QuestionId(UUID.randomUUID()),
                    SystematicStudyId(UUID.randomUUID()),
                    faker.lorem.words(),
                    faker.lorem.words(),
                    10,
                    10
                )
            }
        }

        @Test
        fun `should throw IllegalArgumentException for Higher attribute smaller than Lower`() {
            assertThrows<IllegalArgumentException> {
                NumberScale(
                    QuestionId(UUID.randomUUID()),
                    SystematicStudyId(UUID.randomUUID()),
                    faker.lorem.words(),
                    faker.lorem.words(),
                    1,
                    10
                )
            }
        }

        @ParameterizedTest(name = "[{index}]: value = \"{0}\"")
        @ValueSource(ints = [0, 11, 15])
        fun `should throw IllegalArgumentException for answer out of bounds`(value: Int) {
            assertThrows<IllegalArgumentException> { validNumberScale.answer(value) }
        }
    }
}
