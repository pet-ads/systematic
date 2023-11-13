package br.all.application.study.find

import br.all.application.study.repository.StudyReviewDto
import java.util.*

interface FindAllStudyReviewsService {
    fun findAllFromReview(request: RequestModel) : ResponseModel
    data class RequestModel(val reviewId: UUID)
    data class ResponseModel(val reviewId: UUID, val studyReviews: List<StudyReviewDto>)
}