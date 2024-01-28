package br.all.domain.model.question

import br.all.domain.model.protocol.ProtocolId
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

    @Nested
    @Tag("ValidClasses")
    @DisplayName("When successfully creating Textual questions")
    inner class WhenSuccessfullyCreatingTextualQuestions {
        @Test
        fun `should create a Textual question with valid parameters`() {
            val id = QuestionId(UUID.randomUUID())
            val protocolId = ProtocolId(UUID.randomUUID())
            val code = faker.lorem.words()
            val description = faker.lorem.words()

            assertDoesNotThrow { Textual(id, protocolId, code, description) }
        }

        @Test
        fun `should answer the question with valid text`() {
            val id = QuestionId(UUID.randomUUID())
            val protocolId = ProtocolId(UUID.randomUUID())
            val code = faker.lorem.words()
            val description = faker.lorem.words()
            val textual = Textual(id, protocolId, code, description)
            val value = "TestingAnswer"
            val expectedAnswer = Answer(textual.id.value(), value)

            assertEquals(expectedAnswer, textual.answer(value))
        }
    }

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("When unable to answer successfully to Textual Questions")
    inner class WhenUnableToAnswerSuccessfullyToTextualQuestions {
        @ParameterizedTest(name = "[{index}]: value = \"{0}\"")
        @ValueSource(strings = ["", " ", "  "])
        fun `should throw IllegalArgumentException for blank answer`(value: String) {
            val id = QuestionId(UUID.randomUUID())
            val protocolId = ProtocolId(UUID.randomUUID())
            val code = faker.lorem.words()
            val description = faker.lorem.words()
            val textual = Textual(id, protocolId, code, description)

            assertThrows<IllegalArgumentException> { textual.answer(value) }
        }
    }
}