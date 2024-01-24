package br.all.domain.model.question

import br.all.domain.model.protocol.ProtocolId
import io.github.serpro69.kfaker.Faker
import org.junit.jupiter.api.*
import java.util.*

@Tag("UnitTest")
class LabeledScaleTest {
    private val faker = Faker()

    private val validScale = mapOf(faker.lorem.words() to 1, faker.lorem.words() to 2)
    private val emptyScale = emptyMap<String, Int>()

    @Nested
    @Tag("ValidClasses")
    @DisplayName("When successfully creating LabeledScale questions")
    inner class WhenSuccessfullyCreatingLabeledScaleQuestions {
        @Test
        fun `should create a LabeledScale question with valid parameters`() {
            val id = QuestionId(UUID.randomUUID())
            val protocolId = ProtocolId(UUID.randomUUID())
            val code = faker.lorem.words()
            val description = faker.lorem.words()

            assertDoesNotThrow { LabeledScale(id, protocolId, code, description, validScale) }
        }
    }

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("When unable to create LabeledScale questions")
    inner class WhenUnableToCreateLabeledScaleQuestions {
        @Test
        fun `should throw IllegalArgumentException for empty scales`() {
            val id = QuestionId(UUID.randomUUID())
            val protocolId = ProtocolId(UUID.randomUUID())
            val code = faker.lorem.words()
            val description = faker.lorem.words()

            assertThrows<IllegalArgumentException> { LabeledScale(id, protocolId, code, description, emptyScale) }
        }
    }


}