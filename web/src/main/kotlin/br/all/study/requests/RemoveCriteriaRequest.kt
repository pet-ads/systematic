package br.all.study.requests

import br.all.application.study.update.interfaces.RemoveCriteriaService
import br.all.domain.model.study.StudyReviewStage
import java.util.*

data class RemoveCriteriaRequest (
    val criteria: List<String>,
    val stage: StudyReviewStage
) {
    fun toRequestModel(userId: UUID, systematicStudyId: UUID, studyReviewId: Long, stage: StudyReviewStage) = RemoveCriteriaService.RequestModel (
        userId,
        systematicStudyId,
        studyReviewId,
        criteria,
        stage
    )
}
