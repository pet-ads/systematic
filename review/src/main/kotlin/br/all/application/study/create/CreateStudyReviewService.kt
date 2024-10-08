package br.all.application.study.create

import io.swagger.v3.oas.annotations.media.Schema
import java.util.*


interface CreateStudyReviewService {
    fun createFromStudy(presenter: CreateStudyReviewPresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val searchSessionId: UUID,
        val type: String,
        val title: String,
        val year: Int,
        val authors: String,
        val venue: String,
        val abstract: String,
        val keywords: Set<String>,
        val source: String,
    )

    @Schema(name = "CreateStudyReviewServiceResponseModel")
    open class ResponseModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val studyReviewId: Long
    )
}