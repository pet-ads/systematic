package br.all.application.study.create

import java.util.*


interface CreateStudyReviewService {
    fun createFromStudy(presenter: CreateStudyReviewPresenter, request: RequestModel)

    data class RequestModel(
        val researcherId: UUID,
        val reviewId: UUID,
        val type: String,
        val title: String,
        val year: Int,
        val authors: String,
        val venue: String,
        val abstract: String,
        val keywords: Set<String>,
        val source: String,
    )

    open class ResponseModel(
        val researcherId: UUID,
        val reviewId: UUID,
        val studyId: Long
    )
}