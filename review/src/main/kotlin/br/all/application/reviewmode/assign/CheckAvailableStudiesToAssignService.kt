package br.all.application.reviewmode.assign

import br.all.domain.model.study.StudyReviewStage
import java.util.UUID

interface CheckAvailableStudiesToAssignService {
    fun check(presenter: CheckAvailableStudiesToAssignPresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val stage: StudyReviewStage,
    )

    data class ResponseModel(
        val hasAvailableStudies: Boolean
    )
}
