package br.all.study.requests

import br.all.application.study.update.interfaces.UpdateStudyReviewStatusService
import br.all.domain.model.study.StudyReviewStage
import java.util.*

data class PatchStatusStudyReviewRequest(
    val studyReviewId: List<Long>,
    val status: String,
    val criteria: Set<String>,
    val stage: StudyReviewStage? = null,
) {
    fun toRequestModel(userId: UUID, systematicStudyId: UUID, reviewerId: UUID? = null)
            = UpdateStudyReviewStatusService.RequestModel(
        userId,
        systematicStudyId,
        studyReviewId,
        status,
        this.criteria,
        reviewerId,
        stage,
    )
}