package br.all.study.requests

import br.all.application.study.update.interfaces.MarkAsDuplicatedService
import br.all.domain.model.study.StudyReviewStage
import org.springframework.data.mongodb.core.aggregation.Aggregation.stage
import java.util.*

data class PatchDuplicatedStudiesRequest (
    val duplicatedStudyIds: List<Long>,
    val stage: StudyReviewStage
){
    fun toRequestModel(userId: UUID, systematicStudyId: UUID, studyReviewId: Long)
            = MarkAsDuplicatedService.RequestModel(
        userId,
        systematicStudyId,
        studyReviewId,
        duplicatedStudyIds,
        stage
    )
}