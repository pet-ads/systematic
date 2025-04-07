package br.all.application.study.update.interfaces

import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

interface AnswerExtractionQuestionService {
    fun answerQuestion(presenter: AnswerExtractionQuestionPresenter, request: RequestModel<*>)

    data class RequestModel<T>(
        val userId: UUID,
        val systematicStudyId: UUID,
        val studyReviewId: Long,
        val questionId: UUID,
        val type: String,
        val answer: T,
    )

    data class LabelDto(
        val name: String,
        val value: Int
    )

    @Schema(name = "AnswerExtractionQuestionServiceResponseModel")
    data class ResponseModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val studyReviewId: Long,
    )
}