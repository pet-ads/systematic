package br.all.application.study.find.service

import br.all.application.study.find.presenter.FindStudyReviewPresenter
import br.all.domain.model.study.StudyReviewStage
import java.util.UUID

interface FindReviewerStudyService {
    fun findOne(presenter: FindStudyReviewPresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val reviewerId: UUID,
        val studyReviewId: Long,
        val stage: StudyReviewStage,
    )
}