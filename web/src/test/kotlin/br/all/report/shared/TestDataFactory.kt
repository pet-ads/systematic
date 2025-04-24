package br.all.report.shared

import br.all.domain.model.question.QuestionContextEnum
import br.all.infrastructure.question.MongoQuestionRepository
import br.all.infrastructure.question.QuestionDocument
import br.all.question.utils.TestDataFactory as QuestionFactory
import java.util.*

class TestDataFactory {
    private lateinit var factory: QuestionFactory

    fun createRobQuestions(
        systematicStudyId: UUID,
        repository: MongoQuestionRepository
    ): List<QuestionDocument> {
        factory = QuestionFactory()

        val rq1 = factory.validCreateTextualQuestionDocument(
            questionId = UUID.randomUUID(),
            systematicStudyId = systematicStudyId,
            questionType = QuestionContextEnum.ROB
        )

        val rq2 = factory.validCreateNumberedScaleQuestionDocument(
            questionId = UUID.randomUUID(),
            systematicStudyId = systematicStudyId,
            questionType = QuestionContextEnum.ROB
        )

        val rq3 = factory.validCreateLabeledScaleQuestionDocument(
            questionId = UUID.randomUUID(),
            systematicStudyId = systematicStudyId,
            questionType = QuestionContextEnum.ROB
        )

        val rq4 = factory.validCreatePickListQuestionDocument(
            questionId = UUID.randomUUID(),
            systematicStudyId = systematicStudyId,
            questionType = QuestionContextEnum.ROB
        )

        repository.saveAll(listOf(rq1, rq2, rq3, rq4))
        return listOf(rq1, rq2, rq3, rq4)
    }

    fun deleteRobQuestions(
        repository: MongoQuestionRepository
    ) {
        repository.deleteAll()
    }
}