package br.all.application.study.update.interfaces

import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

interface MarkAsDuplicatedService {

    fun markAsDuplicated(presenter: MarkAsDuplicatedPresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val studyReviewDestination: Long,
        val studyReviewSource: Long,
    )

    @Schema(name = "MarkAsDuplicatedServiceResponseModel")
    class ResponseModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val updatedStudyReview: Long,
        val duplicatedStudyReview: Long,
    )
}