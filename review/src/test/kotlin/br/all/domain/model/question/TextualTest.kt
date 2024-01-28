package br.all.domain.model.question

import br.all.domain.model.protocol.ProtocolId
import br.all.domain.model.study.Answer
import io.github.serpro69.kfaker.Faker
import org.junit.jupiter.api.*
import java.util.*
import kotlin.math.exp
import kotlin.test.assertEquals

@Tag("UnitTest")
class TextualTest{
    private val faker = Faker()

    @Nested
    @Tag("ValidClasses")
    @DisplayName("When successfully creating Textual questions")
    inner class WhenSuccessfullyCreatingTextualQuestions{
        @Test
        fun `should create a Textual question with valid parameters`(){
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

    // TODO: Create answer test 
}