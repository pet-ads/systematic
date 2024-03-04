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
class PickListTest {
    private val faker = Faker()
    private lateinit var validPickList: PickList

    @BeforeEach
    fun setUp() {
        val validOptions = listOf(faker.lorem.words(), faker.lorem.words())
        validPickList = PickList(
            QuestionId(UUID.randomUUID()),
            SystematicStudyId(UUID.randomUUID()),
            faker.lorem.words(),
            faker.lorem.words(),
            validOptions
        )
    }

    @Nested
    @Tag("ValidClasses")
    @DisplayName("When successfully answering a picklist question")
    inner class WhenSuccessfullyCreatingPickListQuestions {
        @Test
        fun `should answer the questions with valid value in the valid options`() {
            val validValue = validPickList.options[1]
            val expectedAnswer = Answer(validPickList.id.value(), validValue)

            assertEquals(expectedAnswer, validPickList.answer(validValue))
        }
    }

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("When being unable to create PickList questions")
    inner class WhenBeingUnableToCreatePickListQuestions {
        @Test
        fun `should throw IllegalArgument exception for empty options list`() {
            assertThrows<IllegalArgumentException> {
                PickList(
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
        fun `should throw IllegalArgument exception for blank option`(option: String) {
            val options = listOf(faker.lorem.words(), option)

            assertThrows<IllegalArgumentException> {
                PickList(
                    QuestionId(UUID.randomUUID()),
                    SystematicStudyId(UUID.randomUUID()),
                    faker.lorem.words(),
                    faker.lorem.words(),
                    options
                )
            }
        }

        @ParameterizedTest(name = "[{index}]: value = \"{0}\"")
        @ValueSource(strings = ["", " ", "  "])
        fun `should throw Illegal Argument Exception for blank value`(value: String) {
            assertThrows<IllegalArgumentException> { validPickList.answer(value) }
        }

        @Test
        fun `should throw Illegal Argument Exception for value not in options`() {
            assertThrows<IllegalArgumentException> { validPickList.answer("word3") }
        }
    }
}
