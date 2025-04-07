package br.all.study.requests

import br.all.application.study.update.interfaces.UpdateStudyReviewStatusService
import java.util.*

data class PatchStatusStudyReviewRequest(
    val studyReviewId: List<Long>,
    val status: String,
    val criteria: Set<String>
) {
    fun toRequestModel(userId: UUID, systematicStudyId: UUID)
    = UpdateStudyReviewStatusService.RequestModel(
        userId,
        systematicStudyId,
        studyReviewId,
        status,
        this.criteria
    )
}