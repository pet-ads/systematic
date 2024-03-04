package br.all.domain.model.question

import br.all.domain.model.protocol.ProtocolId
import br.all.domain.model.review.SystematicStudyId
import io.github.serpro69.kfaker.Faker
import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.util.*

@Tag("UnitTest")
class QuestionBuilderTest {
    private val faker = Faker()
    private lateinit var validQuestionBuilder: QuestionBuilder

    @BeforeEach
    fun setUp(){
        validQuestionBuilder = QuestionBuilder.with(
            QuestionId(UUID.randomUUID()),
            SystematicStudyId(UUID.randomUUID()),
            faker.lorem.words(),
            faker.lorem.words()
        )
    }

    @Nested
    @Tag("ValidClasses")
    @DisplayName("When Successfully building a question")
    inner class WhenSuccessfullyBuildingAQuestion {
        @Test
        fun `should create a Textual question with valid values`() {
            assertDoesNotThrow { validQuestionBuilder.buildTextual() }
        }

        @Test
        fun `should create a PickList question with valid values`() {
            val options = listOf(faker.lorem.words(), faker.lorem.words())

            assertDoesNotThrow { validQuestionBuilder.buildPickList(options) }
        }

        @Test
        fun `should create a NumberScale question with valid values`() {
            val higher = 10
            val lower = 1

            assertDoesNotThrow { validQuestionBuilder.buildNumberScale(lower, higher) }
        }

        @Test
        fun `should create a LabeledScale question with valid values`() {
            val scales = mapOf(faker.lorem.words() to 1, faker.lorem.words() to 2)

            assertDoesNotThrow { validQuestionBuilder.buildLabeledScale(scales) }
        }

        @Nested
        @Tag("InvalidClasses")
        @DisplayName("When unable to create a question")
        inner class WhenUnableToCreateAQuestion {

            @Test
            fun `should not create a PickList question with empty options list`() {
                val options = emptyList<String>()

                assertThrows<IllegalArgumentException> { validQuestionBuilder.buildPickList(options) }
            }

            @ParameterizedTest(name = "[{index}]: item = \"{0}\"")
            @ValueSource(strings = ["", " ", "  "])
            fun `should not create a PickList question with an empty item in the options list`(item: String) {
                val options = listOf(faker.lorem.words(), item)

                assertThrows<IllegalArgumentException> { validQuestionBuilder.buildPickList(options) }
            }

            @ParameterizedTest(name = "[{index}]: lower = \"{0}\"")
            @ValueSource(ints = [11, 15])
            fun `should not create a NumberScale question with lower greater than higher`(lower: Int) {
                val higher = 10

                assertThrows<IllegalArgumentException> { validQuestionBuilder.buildNumberScale(lower, higher) }
            }

            @Test
            fun `should not create a NumberScale question with equal lower and higher values`() {
                val higher = 10
                val lower = 10

                assertThrows<IllegalArgumentException> { validQuestionBuilder.buildNumberScale(lower, higher) }
            }

            @Test
            fun `should not create a LabeledScale question with empty scales`() {
                val scales = emptyMap<String, Int>()

                assertThrows<IllegalArgumentException> { validQuestionBuilder.buildLabeledScale(scales) }
            }
        }
    }
}