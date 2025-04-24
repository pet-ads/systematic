package br.all.report.shared

import br.all.application.report.find.service.FindAnswerService
import br.all.domain.model.question.QuestionContextEnum
import br.all.infrastructure.question.MongoQuestionRepository
import br.all.infrastructure.question.QuestionDocument
import br.all.infrastructure.question.toDto
import br.all.infrastructure.study.StudyReviewDocument
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import br.all.question.utils.TestDataFactory as QuestionFactory
import java.util.*

class TestDataFactory {
    private lateinit var factory: QuestionFactory
    private val mapper = jacksonObjectMapper().registerKotlinModule()


    fun expectedJson(
        userId: UUID,
        question: QuestionDocument,
        review: StudyReviewDocument
    ): String {

        val answerMap: Map<String, List<Long>> = when (question.questionType) {
            "TEXTUAL" -> {
                mapOf("A" to listOf(review.id.studyReviewId))
            }
            "LABELED_SCALE "-> {
                val raw = review.qualityAnswers[question.questionId]!!
                mapOf(raw to listOf(review.id.studyReviewId))
            }
            "NUMBERED_SCALE" -> {
                mapOf("2" to listOf(review.id.studyReviewId))
            }
            "PICK_LIST" -> {
                val option = question.options!!
                mapOf(option.first() to listOf(review.id.studyReviewId))
            }
            else -> emptyMap()
        }

        val dto = FindAnswerService.ResponseModel(
            userId = userId,
            systematicStudyId = question.systematicStudyId,
            question = question.toDto(),
            answer = answerMap
        )
        return mapper.writeValueAsString(dto)
    }

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