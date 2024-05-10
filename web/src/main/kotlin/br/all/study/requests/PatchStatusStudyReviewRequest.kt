package br.all.study.requests

import br.all.application.study.update.interfaces.UpdateStudyReviewStatusService
import java.util.*

data class PatchStatusStudyReviewRequest(
    val status: String
) {
    fun toRequestModel(userId: UUID, systematicStudyId: UUID, studyReviewId: Long)
    = UpdateStudyReviewStatusService.RequestModel(
        userId,
        systematicStudyId,
        studyReviewId,
        status
    )
}