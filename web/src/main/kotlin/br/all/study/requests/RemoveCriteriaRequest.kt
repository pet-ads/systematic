package br.all.study.requests

import br.all.application.study.update.interfaces.RemoveCriteriaService
import java.util.*

data class RemoveCriteriaRequest (
    val criteria: List<String>,
) {
    fun toRequestModel(userId: UUID, systematicStudyId: UUID, studyReviewId: Long) = RemoveCriteriaService.RequestModel (
        userId,
        systematicStudyId,
        studyReviewId,
        criteria
    )
}
