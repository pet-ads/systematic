package br.all.application.report.export.service

import br.all.application.report.export.presenter.ExportReviewPresenter
import java.util.UUID

interface ExportReviewService {
    fun exportReview(presenter: ExportReviewPresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val format: String,
    )

    data class ResponseModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val format: String,
        val formattedReview: String
    )
}