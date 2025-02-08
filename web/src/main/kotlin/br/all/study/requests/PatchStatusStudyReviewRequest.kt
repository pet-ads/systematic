package br.all.study.requests

import br.all.application.study.update.interfaces.UpdateStudyReviewStatusService
import br.all.domain.model.protocol.Criterion
import java.util.*

data class PatchStatusStudyReviewRequest(
    val status: String,
    val criterion: Criterion? = null
) {
    fun toRequestModel(userId: UUID, systematicStudyId: UUID, studyReviewId: Long)
    = UpdateStudyReviewStatusService.RequestModel(
        userId,
        systematicStudyId,
        studyReviewId,
        status,
        criterion
    )
}