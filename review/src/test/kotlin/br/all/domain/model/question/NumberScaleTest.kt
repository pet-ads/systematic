package br.all.domain.model.question

import br.all.domain.model.protocol.ProtocolId
import br.all.domain.model.review.SystematicStudyId
import br.all.domain.model.study.Answer
import io.github.serpro69.kfaker.Faker
import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.util.*
import kotlin.test.assertEquals

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
            val systematicStudyId = SystematicStudyId(UUID.randomUUID())
            val code = faker.lorem.words()
            val description = faker.lorem.words()
            val higher = 10
            val lower = 1

            assertDoesNotThrow { NumberScale(id, systematicStudyId, code, description, higher, lower) }
        }

        @ParameterizedTest(name = "[{index}]: value = \"{0}\"")
        @ValueSource(ints = [1,10,5])
        fun `should answer the questions with valid value equal or between bounds`(value: Int) {
            val id = QuestionId(UUID.randomUUID())
            val systematicStudyId = SystematicStudyId(UUID.randomUUID())
            val code = faker.lorem.words()
            val description = faker.lorem.words()
            val higher = 10
            val lower = 1
            val numberScale = NumberScale(id, systematicStudyId, code, description, higher, lower)
            val expectedAnswer = Answer(numberScale.id.value(), value)

            assertEquals(expectedAnswer, numberScale.answer(value))
        }
    }

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("When being unable to create NumberScale questions")
    inner class WhenBeingUnableToCreateNumberScaleQuestions{
        @Test
        fun `should throw IllegalArgumentException for equal higher and lower values`(){
            val id = QuestionId(UUID.randomUUID())
            val systematicStudyId = SystematicStudyId(UUID.randomUUID())
            val code = faker.lorem.words()
            val description = faker.lorem.words()
            val higher = 10
            val lower = 10

            assertThrows<IllegalArgumentException> { NumberScale(id, systematicStudyId, code, description, higher, lower) }
        }

        @Test
        fun `should throw IllegalArgumentException for Higher attribute smaller than Lower`(){
            val id = QuestionId(UUID.randomUUID())
            val systematicStudyId = SystematicStudyId(UUID.randomUUID())
            val code = faker.lorem.words()
            val description = faker.lorem.words()
            val higher = 5
            val lower = 10

            assertThrows<IllegalArgumentException> { NumberScale(id, systematicStudyId, code, description, higher, lower) }
        }

        @ParameterizedTest(name = "[{index}]: value = \"{0}\"")
        @ValueSource(ints = [0,11,15])
        fun `should throw IllegalArgumentException for answer out of bounds`(value: Int) {
            val id = QuestionId(UUID.randomUUID())
            val systematicStudyId = SystematicStudyId(UUID.randomUUID())
            val code = faker.lorem.words()
            val description = faker.lorem.words()
            val higher = 10
            val lower = 1
            val numberScale = NumberScale(id, systematicStudyId, code, description, higher, lower)

            assertThrows<IllegalArgumentException> { numberScale.answer(value) }
        }
    }
}
