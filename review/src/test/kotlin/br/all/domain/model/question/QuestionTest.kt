package br.all.domain.model.question

import br.all.domain.model.protocol.ProtocolId
import br.all.domain.model.study.Answer
import io.github.serpro69.kfaker.Faker
import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.util.*

@Tag("UnitTest")
class QuestionTest {
    private val faker = Faker()

    @Nested
    @Tag("ValidClasses")
    @DisplayName("When successfully creating questions")
    inner class WhenSuccessfullyCreatingQuestions {
        @Test
        fun `should create a question with valid code and description`() {
            val id = QuestionId(UUID.randomUUID())
            val protocolId = ProtocolId(UUID.randomUUID())
            val code = faker.lorem.words()
            val description = faker.lorem.words()

            assertDoesNotThrow { QuestionImpl(id, protocolId, code, description) }
        }
    }

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("When being unable to create questions")
    inner class WhenBeingUnableToCreateQuestions {
        @ParameterizedTest(name = "[{index}]: code = \"{0}\"")
        @ValueSource(strings = ["", " ", "   "])
        fun `should throw IllegalArgumentException for blank codes`(code: String) {
            val id = QuestionId(UUID.randomUUID())
            val protocolId = ProtocolId(UUID.randomUUID())
            val description = faker.lorem.words()

            assertThrows<IllegalArgumentException> { QuestionImpl(id, protocolId, code, description) }
        }

        @ParameterizedTest(name = "[{index}]: description = \"{0}\"")
        @ValueSource(strings = ["", " ", "   "])
        fun `should throw IllegalArgumentException for blank descriptions`(description: String) {
            val id = QuestionId(UUID.randomUUID())
            val protocolId = ProtocolId(UUID.randomUUID())
            val code = faker.lorem.words()

            assertThrows<IllegalArgumentException> { QuestionImpl(id, protocolId, code, description) }
        }

        @Test
        fun `should throw IllegalArgumentException for blank codes and descriptions`() {
            val id = QuestionId(UUID.randomUUID())
            val protocolId = ProtocolId(UUID.randomUUID())
            val code = ""
            val description = ""

            assertThrows<IllegalArgumentException> { QuestionImpl(id, protocolId, code, description) }
        }
    }

    private class QuestionImpl(
        id: QuestionId,
        protocolId: ProtocolId,
        code: String,
        description: String,
    ): Question<Int>(id, protocolId, code, description) {
        override fun answer(value: Int) = Answer(id.value(), value)
    }
}
