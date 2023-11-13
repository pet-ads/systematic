package br.all.application.study.create

import java.util.*


interface CreateStudyReviewService {
    fun createFromStudy(reviewId: UUID, study: RequestModel)

    data class RequestModel (
        val type: String,
        val title: String,
        val year: Int,
        val authors: String,
        val venue: String,
        val abstract: String,
        val keywords: Set<String>,
        val source: String,
    )

    open class ResponseModel(val reviewId: UUID, val studyId: Long)
}

