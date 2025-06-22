package br.all.report.shared

import br.all.application.report.find.service.FindAnswerService
import br.all.domain.model.question.QuestionContextEnum
import br.all.infrastructure.question.MongoQuestionRepository
import br.all.infrastructure.question.QuestionDocument
import br.all.infrastructure.question.toDto
import br.all.study.utils.TestDataFactory as StudyReviewTestDataFactory
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
                mapOf("Resposta: ${question.description}" to listOf(review.id.studyReviewId))
            }
            "LABELED_SCALE"-> {
                val raw: String = if (question.context == QuestionContextEnum.ROB) {
                    review.qualityAnswers[question.questionId]!!
                } else {
                    review.formAnswers[question.questionId]!!
                }

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

    fun createQuestions(
        systematicStudyId: UUID,
        repository: MongoQuestionRepository,
        context: QuestionContextEnum
    ): List<QuestionDocument> {
        factory = QuestionFactory()

        val rq1 = factory.validCreateTextualQuestionDocument(
            questionId = UUID.randomUUID(),
            systematicStudyId = systematicStudyId,
            questionType = context
        )

        val rq2 = factory.validCreateNumberedScaleQuestionDocument(
            questionId = UUID.randomUUID(),
            systematicStudyId = systematicStudyId,
            questionType = context
        )

        val rq3 = factory.validCreateLabeledScaleQuestionDocument(
            questionId = UUID.randomUUID(),
            systematicStudyId = systematicStudyId,
            questionType = context
        )

        val rq4 = factory.validCreatePickListQuestionDocument(
            questionId = UUID.randomUUID(),
            systematicStudyId = systematicStudyId,
            questionType = context
        )

        repository.saveAll(listOf(rq1, rq2, rq3, rq4))
        return listOf(rq1, rq2, rq3, rq4)
    }

    fun deleteQuestions(
        repository: MongoQuestionRepository
    ) {
        repository.deleteAll()
    }

    fun createStudyReviewWithQuestions(
        systematicStudyId: UUID,
        robQuestions: List<QuestionDocument>,
        extractionQuestions: List<QuestionDocument>,
    ): StudyReviewDocument {
        val studyReviewDataFactory = StudyReviewTestDataFactory()

        val (robKey, robValue) = robQuestions[2].scales!!.entries.first()
        val (extractionKey, extractionValue) = extractionQuestions[2].scales!!.entries.first()
        val extractionOptions = extractionQuestions[3].options!!
        val robOptions = robQuestions[3].options!!

        val studyReview = studyReviewDataFactory.reviewDocument(
            systematicStudyId,
            studyReviewId = 11111,
            robAnswers = mapOf(
                robQuestions[0].questionId to "Resposta: ${robQuestions[0].description}",
                robQuestions[1].questionId to "2",
                robQuestions[2].questionId to "Label(name: $robKey, value: $robValue)",
                robQuestions[3].questionId to robOptions.first()
            ),
            formAnswers = mapOf(
                extractionQuestions[0].questionId to "Resposta: ${extractionQuestions[0].description}",
                extractionQuestions[1].questionId to "2",
                extractionQuestions[2].questionId to "Label(name: $extractionKey, value: $extractionValue)",
                extractionQuestions[3].questionId to extractionOptions.first(),
            )
        )

        return studyReview
    }


}