package br.all.reviewmode.requests

import br.all.application.reviewmode.assign.CheckAvailableStudiesToAssignService
import br.all.domain.model.study.StudyReviewStage
import java.util.UUID

data class GetCheckAvailableStudiesRequest(
    val stage: StudyReviewStage,
) {
    fun toRequestModel(userId: UUID, systematicStudyId: UUID) =
        CheckAvailableStudiesToAssignService.RequestModel(
            userId = userId,
            systematicStudyId = systematicStudyId,
            stage = stage,
        )
}
