package br.all.application.study.find

import br.all.application.study.repository.StudyReviewDto
import java.util.*

interface FindStudyReviewService {
    fun findOne(request: RequestModel) : ResponseModel
    data class RequestModel(val reviewId: UUID, val studyReviewId: Long)
    data class ResponseModel(val content: StudyReviewDto)
}