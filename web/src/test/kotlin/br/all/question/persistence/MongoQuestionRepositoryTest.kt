package br.all.question.persistence

import br.all.application.question.create.CreateQuestionService.QuestionType.*
import br.all.infrastructure.question.MongoQuestionRepository
import br.all.infrastructure.question.QuestionDocument
import br.all.infrastructure.shared.toNullable
import br.all.question.utils.TestDataFactory
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.UUID

@SpringBootTest
class MongoQuestionRepositoryTest(
    @Autowired private val sut: MongoQuestionRepository,
) {
    private lateinit var factory: TestDataFactory
    private lateinit var questionId: UUID
    private lateinit var systematicStudyId: UUID

    @BeforeEach
    fun setUp() {
        factory = TestDataFactory()
        questionId = UUID.randomUUID()
        systematicStudyId = UUID.randomUUID()
        sut.deleteAll()
    }

    @AfterEach
    fun teardown() = sut.deleteAll()

    private fun insertAndCheck(question: QuestionDocument) {
        sut.insert(question)
        assertTrue(sut.findById(question.questionId).toNullable() != null)
    }

    private fun updateAndCheck(questionToUpdate: QuestionDocument) {
        sut.insert(questionToUpdate)
        val updatedDescription = factory.fakerWord
        val updatedQuestion = when (questionToUpdate.questionType) {
            TEXTUAL.toString() -> factory.validCreateTextualQuestionDocument(
                questionId,
                systematicStudyId,
                description = updatedDescription
            )

            PICK_LIST.toString() -> factory.validCreatePickListQuestionDocument(
                questionId,
                systematicStudyId,
                description = updatedDescription
            )

            LABELED_SCALE.toString() -> factory.validCreateLabeledScaleQuestionDocument(
                questionId,
                systematicStudyId,
                description = updatedDescription
            )

            NUMBERED_SCALE.toString() -> factory.validCreateNumberedScaleQuestionDocument(
                questionId,
                systematicStudyId,
                description = updatedDescription
            )

            else -> throw IllegalArgumentException("Question type ${questionToUpdate.questionType} does not exist.")
        }
        sut.save(updatedQuestion)

        assertEquals(updatedDescription, sut.findById(questionId).toNullable()?.description)
    }

    @Nested
    @DisplayName("when successfully inserting questions")
    inner class WhenSuccessfullyInsertingQuestions {
        @Test
        fun `should insert a textual question`() {
            insertAndCheck(factory.validCreateTextualQuestionDocument(questionId, systematicStudyId))
        }

        @Test
        fun `should insert a picklist question`() {
            insertAndCheck(factory.validCreatePickListQuestionDocument(questionId, systematicStudyId))
        }

        @Test
        fun `should insert a labeledScale question`() {
            insertAndCheck(factory.validCreateLabeledScaleQuestionDocument(questionId, systematicStudyId))
        }

        @Test
        fun `should insert a numberScale question`() {
            insertAndCheck(factory.validCreateNumberedScaleQuestionDocument(questionId, systematicStudyId))
        }
    }

    @Nested
    @DisplayName("when successfully updating questions")
    inner class WhenSuccessfullyUpdatingQuestions {
        @Test
        fun `should update a textual question`() {
            val textualQuestion = factory.validCreateTextualQuestionDocument(questionId, systematicStudyId)
            updateAndCheck(textualQuestion)
        }

        @Test
        fun `should update a picklist question`() {
            val pickListQuestion = factory.validCreatePickListQuestionDocument(questionId, systematicStudyId)
            updateAndCheck(pickListQuestion)
        }

        @Test
        fun `should update a labeledScale question`() {
            val labeledScaleQuestion = factory.validCreateLabeledScaleQuestionDocument(questionId, systematicStudyId)
            updateAndCheck(labeledScaleQuestion)
        }

        @Test
        fun `should update a numberScale question`() {
            val numberScaleQuestion = factory.validCreateNumberedScaleQuestionDocument(questionId, systematicStudyId)
            updateAndCheck(numberScaleQuestion)
        }
    }

    @Nested
    @DisplayName("when successfully finding questions")
    inner class WhenSuccessfullyFindingQuestions {
        @Test
        fun `should find a textual question`() {
            val textualQuestion = factory.validCreateTextualQuestionDocument(questionId, systematicStudyId)
            sut.insert(textualQuestion)
            val result = sut.findById(textualQuestion.questionId)

            assertEquals(textualQuestion.questionId, result.toNullable()?.questionId)
        }

        @Test
        fun `should find a picklist question`() {
            val pickListQuestion = factory.validCreatePickListQuestionDocument(questionId, systematicStudyId)
            sut.insert(pickListQuestion)
            val result = sut.findById(pickListQuestion.questionId)

            assertEquals(pickListQuestion.questionId, result.toNullable()?.questionId)
        }

        @Test
        fun `should find a labeledScale question`() {
            val labeledScaleQuestion = factory.validCreateLabeledScaleQuestionDocument(questionId, systematicStudyId)
            sut.insert(labeledScaleQuestion)
            val result = sut.findById(labeledScaleQuestion.questionId)

            assertEquals(labeledScaleQuestion.questionId, result.toNullable()?.questionId)
        }

        @Test
        fun `should find a numberScale question`() {
            val numberScaleQuestion = factory.validCreateNumberedScaleQuestionDocument(questionId, systematicStudyId)
            sut.insert(numberScaleQuestion)
            val result = sut.findById(numberScaleQuestion.questionId)

            assertEquals(numberScaleQuestion.questionId, result.toNullable()?.questionId)
        }

        @Test
        fun `should find all questions of a systematicStudy`() {
            val textualQuestion = factory.validCreateTextualQuestionDocument(questionId, systematicStudyId)
            val pickListQuestion = factory.validCreatePickListQuestionDocument(UUID.randomUUID(), systematicStudyId)
            val numberedScaleQuestion =
                factory.validCreateNumberedScaleQuestionDocument(UUID.randomUUID(), systematicStudyId)
            val labeledScaleQuestion =
                factory.validCreateLabeledScaleQuestionDocument(UUID.randomUUID(), systematicStudyId)

            sut.insert(textualQuestion)
            sut.insert(pickListQuestion)
            sut.insert(numberedScaleQuestion)
            sut.insert(labeledScaleQuestion)

            val result = sut.findAllBySystematicStudyId(systematicStudyId)
            assertTrue(result.size == 4)
        }
    }

}

