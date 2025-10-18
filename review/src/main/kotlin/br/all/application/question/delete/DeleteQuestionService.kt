package br.all.application.question.delete

import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

interface DeleteQuestionService {
    fun delete(presenter: DeleteQuestionPresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val questionId: UUID,
    )

    @Schema(name = "DeleteQuestionResponseModel")
    data class ResponseModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val questionId: UUID,
        val affectedStudyReviewIds: List<Long>
    )
}