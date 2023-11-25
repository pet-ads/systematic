package br.all.application.study.find.service

import br.all.application.study.repository.StudyReviewDto
import java.util.*

interface FindStudyReviewService {
    fun findOne(request: RequestModel)
    data class RequestModel(val researcherId: UUID, val systematicStudy: UUID, val studyReviewId: Long)
    data class ResponseModel(
        val researcherId: UUID,
        val content: StudyReviewDto
    )
}