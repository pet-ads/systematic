package br.all.application.study.update.interfaces

import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

interface AnswerQuestionService {
    fun answerQuestion(presenter: AnswerQuestionPresenter, request: RequestModel<*>)

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

    @Schema(name = "AnswerQuestionServiceResponseModel")
    data class ResponseModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val studyReviewId: Long,
    )
}