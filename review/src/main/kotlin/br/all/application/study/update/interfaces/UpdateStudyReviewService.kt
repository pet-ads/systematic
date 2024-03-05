package br.all.application.study.update.interfaces

import java.util.*


interface UpdateStudyReviewService {
    fun updateFromStudy(presenter: UpdateStudyReviewPresenter, request: RequestModel)

    data class RequestModel(
        val researcherId: UUID,
        val systematicStudyId: UUID,
        val type: String,
        val title: String,
        val year: Int,
        val authors: String,
        val venue: String,
        val abstract: String,
        val keywords: Set<String>,
        val source: String,
        val studyReviewId: Long,
    )

    open class ResponseModel(
        val researcherId: UUID,
        val systematicStudyId: UUID,
        val studyReviewId: Long
    )
}