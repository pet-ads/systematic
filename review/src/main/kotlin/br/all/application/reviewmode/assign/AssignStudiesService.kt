package br.all.application.reviewmode.assign

import br.all.domain.model.study.StudyReviewStage
import java.util.UUID

interface AssignStudiesService {
    fun assign(presenter: AssignStudiesPresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val stage: StudyReviewStage,
    )

    class ResponseModel
}