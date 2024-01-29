package br.all.domain.model.question

import br.all.domain.model.protocol.ProtocolId
import io.github.serpro69.kfaker.Faker
import org.junit.jupiter.api.*
import java.util.*

@Tag("UnitTest")
class QuestionBuilderTest{
    private val faker = Faker()

    @Nested
    @Tag("ValidClasses")
    @DisplayName("When Successfully building a question")
    inner class WhenSuccessfullyBuildingAQuestion{
        @Test
        fun `should create a textual question with valid values`() {
            val id = QuestionId(UUID.randomUUID())
            val protocolId = ProtocolId(UUID.randomUUID())
            val code = faker.lorem.words()
            val description = faker.lorem.words()
            val question = buildQuestion(id, protocolId, code, description)

            assertDoesNotThrow { question.buildTextual() }
        }

    }

    fun buildQuestion(id: QuestionId, protocolId: ProtocolId, code: String, description: String) =
        QuestionBuilder.with(id, protocolId, code, description)
}