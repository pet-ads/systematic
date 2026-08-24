package br.all.application.study.update.interfaces

import br.all.domain.model.study.StudyReviewStage
import java.util.*


interface RemoveCriteriaService {
    fun removeCriteria(presenter: RemoveCriteriaPresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val studyId: Long,
        val criteria: List<String>,
        val stage: StudyReviewStage
    )

    data class ResponseModel(
        val systematicStudyId: UUID,
        val studyId: Long,
        val stage: StudyReviewStage,
        val inclusionCriteria: List<String>,
        val exclusionCriteria: List<String>,
    )
}
