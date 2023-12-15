package br.all.application.study.update.interfaces

import java.util.UUID

interface MarkAsDuplicatedService {

    fun markAsDuplicated(presenter: MarkAsDuplicatedPresenter, request: RequestModel)

    data class RequestModel(
        val researcherId: UUID,
        val systematicStudyId: UUID,
        val studyReviewDestination: Long,
        val studyReviewSource: Long,
    )

    class ResponseModel(
        val researcherId: UUID,
        val systematicStudyId: UUID,
        val updatedStudyReview: Long,
        val duplicatedStudyReview: Long,
    )
}