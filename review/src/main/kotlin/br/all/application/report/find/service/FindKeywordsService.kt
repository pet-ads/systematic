package br.all.application.report.find.service

import br.all.application.report.find.presenter.FindKeywordsPresenter
import java.util.*

interface FindKeywordsService {
    fun findKeywords(presenter: FindKeywordsPresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val studyReviewId: Long,
        val filter: String?,
    )

    data class ResponseModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val studyReviewId: Long,
        val keywords: List<String>,
        val keywordsQuantity: Int,
        val filter: String?,
    )
}