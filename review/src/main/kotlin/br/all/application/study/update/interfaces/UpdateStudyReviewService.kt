package br.all.application.study.update.interfaces

import io.swagger.v3.oas.annotations.media.Schema
import java.util.*


interface UpdateStudyReviewService {
    fun updateFromStudy(presenter: UpdateStudyReviewPresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID,
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

    @Schema(name = "UpdateStudyReviewService")
    open class ResponseModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val studyReviewId: Long
    )
}