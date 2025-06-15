package br.all.domain.services

import br.all.domain.model.study.StudyReview

interface SelectionStatusSuggestionService {
    fun buildSuggestions(request: RequestModel) : ResponseModel

    data class RequestModel(val studiesInfo: List<StudyReviewInfo>)

    data class ResponseModel(val studySuggestions: List<StudyReview>)

    data class StudyReviewInfo(
        val studyReviewId: Long,
        val studyReviewScore: Int,
        val studyReviewCitations: Int,
        val studyReviewYear: Int
    )

    data class StudyStatusSuggestion(
        val studyReviewId: Long,
        val studyReviewScore: String,
    )
}