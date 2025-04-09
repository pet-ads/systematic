package br.all.study.requests

import br.all.application.study.update.interfaces.MarkAsDuplicatedService
import java.util.*

data class PatchDuplicatedStudiesRequest (
    val duplicatedStudyIds: List<Long>,
){
    fun toRequestModel(userId: UUID, systematicStudyId: UUID, studyReviewId: Long)
            = MarkAsDuplicatedService.RequestModel(
        userId,
        systematicStudyId,
        studyReviewId,
        duplicatedStudyIds
    )
}