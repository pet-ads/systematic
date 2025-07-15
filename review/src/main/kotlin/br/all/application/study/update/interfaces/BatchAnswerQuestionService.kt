package br.all.application.study.update.interfaces

import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

interface BatchAnswerQuestionService {
    fun batchAnswerQuestion(presenter: BatchAnswerQuestionPresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val studyReviewId: Long,
        val answers: List<AnswerDetail>
    ) {
        data class AnswerDetail(
            val questionId: UUID,
            val type: String,
            val answer: Any
        )
    }

    data class FailedAnswer(
        val questionId: UUID,
        val reason: String
    )

    data class LabelDto(
        val name: String,
        val value: Int
    )

    @Schema(name = "BatchAnswerQuestionServiceResponseModel")
    data class ResponseModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val studyReviewId: Long,
        val succeededAnswers: List<UUID>,
        val failedAnswers: List<FailedAnswer>,
        val totalAnswered: Int
    )
}