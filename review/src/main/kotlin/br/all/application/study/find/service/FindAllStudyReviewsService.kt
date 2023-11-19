package br.all.application.study.find.service

import br.all.application.study.repository.StudyReviewDto
import java.util.*

interface FindAllStudyReviewsService {
    fun findAllFromReview(request: RequestModel)
    data class RequestModel(
        val researcherId: UUID,
        val reviewId: UUID
    )
    data class ResponseModel(
        val researcherId: UUID,
        val reviewId: UUID,
        val studyReviews: List<StudyReviewDto>
    )
}