package br.all.domain.model.question

import br.all.domain.model.protocol.ProtocolId
import io.github.serpro69.kfaker.Faker
import org.junit.jupiter.api.*
import java.util.*

@Tag("UnitTest")
class NumberScaleTest{
    private val faker = Faker()

    @Nested
    @Tag("ValidClasses")
    @DisplayName("When successfully creating NumberScale questions")
    inner class WhenSuccessfullyCreatingNumberScaleQuestions{
        @Test
        fun `should create a NumberScale question with valid options`(){
            val id = QuestionId(UUID.randomUUID())
            val protocolId = ProtocolId(UUID.randomUUID())
            val code = faker.lorem.words()
            val description = faker.lorem.words()
            val higher = 10
            val lower = 1

            assertDoesNotThrow { NumberScale(id, protocolId, code, description, higher, lower) }
        }
    }

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("When being unable to create NumberScale questions")
    inner class WhenBeingUnableToCreateNumberScaleQuestions{
        @Test
        fun `should throw IllegalArgumentException for equal higher and lower values`(){
            val id = QuestionId(UUID.randomUUID())
            val protocolId = ProtocolId(UUID.randomUUID())
            val code = faker.lorem.words()
            val description = faker.lorem.words()
            val higher = 10
            val lower = 10

            assertThrows<IllegalArgumentException> { NumberScale(id, protocolId, code, description, higher, lower) }
        }

        @Test
        fun `should throw IllegalArgumentException for Higher attribute smaller than Lower`(){
            val id = QuestionId(UUID.randomUUID())
            val protocolId = ProtocolId(UUID.randomUUID())
            val code = faker.lorem.words()
            val description = faker.lorem.words()
            val higher = 5
            val lower = 10

            assertThrows<IllegalArgumentException> { NumberScale(id, protocolId, code, description, higher, lower) }
        }

        // TODO: answer test 
    }
}
