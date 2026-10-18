package br.all.application.reviewmode.find

import br.all.domain.model.reviewmode.ReviewMode
import java.util.UUID

interface FindReviewModeService {
    fun find(presenter: FindReviewModePresenter, request: RequestModel)

    data class RequestModel(val userId: UUID, val systematicStudyId: UUID)

    data class ResponseModel(
        val systematicStudyId: UUID,
        val selection: ReviewMode?,
        val extraction: ReviewMode?,
    )
}