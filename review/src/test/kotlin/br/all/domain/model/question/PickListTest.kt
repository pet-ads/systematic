package br.all.domain.model.question

import br.all.domain.model.protocol.ProtocolId
import io.github.serpro69.kfaker.Faker
import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.util.*

@Tag("UnitTest")
class PickListTest {
    private val faker = Faker()

    @Nested
    @Tag("ValidClasses")
    @DisplayName("When successfully creating PickList questions")
    inner class WhenSuccessfullyCreatingPickListQuestions{
        @Test
        fun `should create a PickList question with valid options`(){
            val id = QuestionId(UUID.randomUUID())
            val protocolId = ProtocolId(UUID.randomUUID())
            val code = faker.lorem.words()
            val description = faker.lorem.words()
            val options = listOf(faker.lorem.words(),faker.lorem.words())

            assertDoesNotThrow { PickList(id, protocolId, code, description, options) }
        }
    }

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("When being unable to create PickList questions")
    inner class WhenBeingUnableToCreatePickListQuestions{
        @Test
        fun `should throw IllegalArgument exception for empty options list` (){
            val id = QuestionId(UUID.randomUUID())
            val protocolId = ProtocolId(UUID.randomUUID())
            val code = faker.lorem.words()
            val description = faker.lorem.words()
            val options = emptyList<String>()

            assertThrows<IllegalArgumentException> { PickList(id, protocolId, code, description, options) }
        }

        @ParameterizedTest(name = "[{index}]: option = \"{0}\"")
        @ValueSource(strings = ["", " ", "  "])
        fun `should throw IllegalArgument exception for blank option` (option: String){
            val id = QuestionId(UUID.randomUUID())
            val protocolId = ProtocolId(UUID.randomUUID())
            val code = faker.lorem.words()
            val description = faker.lorem.words()
            val options = listOf(faker.lorem.words(), option)

            assertThrows<IllegalArgumentException> { PickList(id, protocolId, code, description, options) }
        }

        // TODO: Answer Test
    }
}
