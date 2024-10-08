package br.all.study.requests

import br.all.application.study.create.CreateStudyReviewService
import java.util.*

data class PostStudyReviewRequest(
    val searchSessionId: UUID,
    val type: String,
    val title: String,
    val year: Int,
    val authors: String,
    val venue: String,
    val abstract: String,
    val keywords: Set<String>,
    val source: String,
) {
    fun toRequestModel(userId: UUID, systematicStudyId: UUID) = CreateStudyReviewService.RequestModel(
        userId,
        systematicStudyId,
        searchSessionId,
        type,
        title,
        year,
        authors,
        venue,
        abstract,
        keywords,
        source
    )
}