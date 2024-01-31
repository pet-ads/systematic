package br.all.domain.model.protocol

import org.junit.jupiter.api.*

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
}
