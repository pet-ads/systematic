package br.all.domain.model.question

import br.all.domain.model.review.SystematicStudyId
import br.all.domain.model.study.Answer
import io.github.serpro69.kfaker.Faker
import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.util.*
import kotlin.test.assertEquals

@Tag("UnitTest")
class PickManyTest {
    private val faker = Faker()
    private lateinit var validPickMany: PickMany

    @BeforeEach
    fun setUp() {
        val validOptions = listOf(faker.lorem.words(), faker.lorem.words(), faker.lorem.words())
        validPickMany = PickMany(
            QuestionId(UUID.randomUUID()),
            SystematicStudyId(UUID.randomUUID()),
            faker.lorem.words(),
            faker.lorem.words(),
            validOptions
        )
    }

    @Nested
    @Tag("ValidClasses")
    @DisplayName("When successfully answering a pick-many question")
    inner class WhenSuccessfullyAnsweringPickManyQuestion {
        @Test
        fun `should answer with a single valid option from the list`() {
            val chosenAnswer = listOf(validPickMany.options[1])
            val expectedAnswer = Answer(validPickMany.id.value(), chosenAnswer)

            val actualAnswer = validPickMany.answer(chosenAnswer)

            assertEquals(expectedAnswer, actualAnswer)
        }

        @Test
        fun `should answer with multiple valid options from the list`() {
            val chosenAnswer = listOf(validPickMany.options[0], validPickMany.options[2])
            val expectedAnswer = Answer(validPickMany.id.value(), chosenAnswer)

            val actualAnswer = validPickMany.answer(chosenAnswer)

            assertEquals(expectedAnswer, actualAnswer)
        }
    }

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("When being unable to create or answer PickMany questions")
    inner class WhenBeingUnableToCreateOrAnswerPickMany {

        @Test
        fun `should throw IllegalArgumentException for an empty options list during creation`() {
            assertThrows<IllegalArgumentException> {
                PickMany(
                    QuestionId(UUID.randomUUID()),
                    SystematicStudyId(UUID.randomUUID()),
                    faker.lorem.words(),
                    faker.lorem.words(),
                    emptyList()
                )
            }
        }

        @ParameterizedTest(name = "[{index}]: option = \"{0}\"")
        @ValueSource(strings = ["", " ", "  "])
        fun `should throw IllegalArgumentException for a blank option during creation`(option: String) {
            val options = listOf(faker.lorem.words(), option)

            assertThrows<IllegalArgumentException> {
                PickMany(
                    QuestionId(UUID.randomUUID()),
                    SystematicStudyId(UUID.randomUUID()),
                    faker.lorem.words(),
                    faker.lorem.words(),
                    options
                )
            }
        }

        @Test
        fun `should throw IllegalArgumentException for an empty answer list`() {
            assertThrows<IllegalArgumentException> { validPickMany.answer(emptyList()) }
        }

        @ParameterizedTest(name = "[{index}]: value = \"{0}\"")
        @ValueSource(strings = ["", " ", "  "])
        fun `should throw IllegalArgumentException for a blank value in the answer list`(value: String) {
            val invalidAnswer = listOf(validPickMany.options.first(), value)

            assertThrows<IllegalArgumentException> { validPickMany.answer(invalidAnswer) }
        }

        @Test
        fun `should throw IllegalArgumentException for a value in the answer list that is not in options`() {
            val invalidAnswer = listOf(validPickMany.options.first(), "invalid option")

            assertThrows<IllegalArgumentException> { validPickMany.answer(invalidAnswer) }
        }
    }
}