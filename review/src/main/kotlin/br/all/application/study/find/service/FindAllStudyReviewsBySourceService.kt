package br.all.application.study.find.service

import br.all.application.study.find.presenter.FindAllStudyReviewsBySourcePresenter
import br.all.application.study.repository.StudyReviewDto
import java.util.*

interface FindAllStudyReviewsBySourceService {
    fun findAllFromSearchSession(presenter: FindAllStudyReviewsBySourcePresenter, request: RequestModel)

    data class RequestModel(
        val researcherId: UUID,
        val systematicStudyId: UUID,
        val searchSource: String,
    )
    data class ResponseModel(
        val researcherId: UUID,
        val systematicStudyId: UUID,
        val searchSource: String,
        val studyReviews: List<StudyReviewDto>
    )
}