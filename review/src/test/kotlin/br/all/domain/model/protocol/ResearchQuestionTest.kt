package br.all.domain.model.protocol

import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

@Tag("UnitClass")
class ResearchQuestionTest {
    @Nested
    @Tag("ValidClasses")
    @DisplayName("When being succeed to create ResearchQuestions")
    inner class WhenBeingSucceedToCreateResearchQuestions {
        @Test
        fun `should create a valid ResearchQuestion`() {
            assertDoesNotThrow { ResearchQuestion("Valid description") }
        }

        @Test
        fun `should convert a valid String to a ResearchQuestion`() {
            assertDoesNotThrow { "Valid description".toResearchQuestion() }
        }
    }

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("When providing invalid descriptions")
    inner class WhenProvidingInvalidDescriptions {
        @ParameterizedTest(name = "[{index}]: description=\"{0}\"")
        @ValueSource(strings = ["", " ", "   "])
        fun `should throw when the description is blank`(description: String) {
            assertThrows<IllegalArgumentException> { ResearchQuestion(description) }
        }

        @ParameterizedTest(name = "[{index}]: string=\"{0}\"")
        @ValueSource(strings = ["", " ", "   "])
        fun `should throw when trying to transform blank Strings into ResearchQuestions`(string: String) {
            assertThrows<IllegalArgumentException> { string.toResearchQuestion() }
        }
    }
}
