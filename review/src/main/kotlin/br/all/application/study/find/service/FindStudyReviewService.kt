package br.all.application.study.find.service

import br.all.application.study.find.presenter.FindStudyReviewPresenter
import br.all.application.study.repository.StudyReviewDto
import java.util.*

interface FindStudyReviewService {
    fun findOne(presenter: FindStudyReviewPresenter, request: RequestModel)
    data class RequestModel(val researcherId: UUID, val reviewId: UUID, val studyReviewId: Long)
    data class ResponseModel(
        val researcherId: UUID,
        val content: StudyReviewDto
    )
}