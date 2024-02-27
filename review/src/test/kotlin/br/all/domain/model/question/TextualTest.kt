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
class TextualTest {
    private val faker = Faker()
    private lateinit var validTextual: Textual

    @BeforeEach
    fun setUp() {
        validTextual = Textual(
            QuestionId(UUID.randomUUID()),
            SystematicStudyId(UUID.randomUUID()),
            faker.lorem.words(),
            faker.lorem.words(),
        )
    }

    @Nested
    @Tag("ValidClasses")
    @DisplayName("When successfully answering Textual questions")
    inner class WhenSuccessfullyCreatingTextualQuestions {

        @Test
        fun `should answer the question with valid text`() {
            val value = "TestingAnswer"
            val expectedAnswer = Answer(validTextual.id.value(), value)

            assertEquals(expectedAnswer, validTextual.answer(value))
        }
    }

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("When unable to answer successfully to Textual Questions")
    inner class WhenUnableToAnswerSuccessfullyToTextualQuestions {
        @ParameterizedTest(name = "[{index}]: value = \"{0}\"")
        @ValueSource(strings = ["", " ", "  "])
        fun `should throw IllegalArgumentException for blank answer`(value: String) {
            assertThrows<IllegalArgumentException> { validTextual.answer(value) }
        }
    }
}