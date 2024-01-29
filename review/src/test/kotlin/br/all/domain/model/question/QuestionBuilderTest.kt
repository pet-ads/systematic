package br.all.domain.model.question

import br.all.domain.model.protocol.ProtocolId
import io.github.serpro69.kfaker.Faker
import org.junit.jupiter.api.*
import java.util.*

@Tag("UnitTest")
class QuestionBuilderTest {
    private val faker = Faker()

    @Nested
    @Tag("ValidClasses")
    @DisplayName("When Successfully building a question")
    inner class WhenSuccessfullyBuildingAQuestion {
        @Test
        fun `should create a Textual question with valid values`() {
            val id = QuestionId(UUID.randomUUID())
            val protocolId = ProtocolId(UUID.randomUUID())
            val code = faker.lorem.words()
            val description = faker.lorem.words()
            val question = buildQuestion(id, protocolId, code, description)

            assertDoesNotThrow { question.buildTextual() }
        }

        @Test
        fun `should create a PickList question with valid values`() {
            val id = QuestionId(UUID.randomUUID())
            val protocolId = ProtocolId(UUID.randomUUID())
            val code = faker.lorem.words()
            val description = faker.lorem.words()
            val question = buildQuestion(id, protocolId, code, description)
            val options = listOf(faker.lorem.words(), faker.lorem.words())

            assertDoesNotThrow { question.buildPickList(options) }
        }

        @Test
        fun `should create a NumberScale question with valid values`() {
            val id = QuestionId(UUID.randomUUID())
            val protocolId = ProtocolId(UUID.randomUUID())
            val code = faker.lorem.words()
            val description = faker.lorem.words()
            val question = buildQuestion(id, protocolId, code, description)
            val higher = 10
            val lower = 1

            assertDoesNotThrow { question.buildNumberScale(higher, lower) }
        }

        @Test
        fun `should create a LabeledScale question with valid values`() {
            val id = QuestionId(UUID.randomUUID())
            val protocolId = ProtocolId(UUID.randomUUID())
            val code = faker.lorem.words()
            val description = faker.lorem.words()
            val question = buildQuestion(id, protocolId, code, description)
            val scales = mapOf(faker.lorem.words() to 1, faker.lorem.words() to 2)

            assertDoesNotThrow { question.buildLabeledScale(scales) }
        }
    }

    fun buildQuestion(id: QuestionId, protocolId: ProtocolId, code: String, description: String) =
        QuestionBuilder.with(id, protocolId, code, description)
}