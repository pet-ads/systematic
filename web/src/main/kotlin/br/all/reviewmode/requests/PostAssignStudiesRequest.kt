package br.all.reviewmode.requests

import br.all.application.reviewmode.assign.AssignStudiesService
import br.all.domain.model.study.StudyReviewStage
import java.util.UUID

data class PostAssignStudiesRequest(
    val stage: StudyReviewStage,
) {
    fun toRequestModel(userId: UUID, systematicStudyId: UUID) =
        AssignStudiesService.RequestModel(
            userId = userId,
            systematicStudyId = systematicStudyId,
            stage = stage,
        )
}
