package br.all.application.study.find.service

import br.all.application.study.find.presenter.FindAllStudyReviewsByAdvancedSearchPresenter
import br.all.application.study.repository.StudyReviewDto
import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

interface FindAllStudyReviewsByAdvancedSearchService {

    fun findAllByAdvancedSearch(presenter: FindAllStudyReviewsByAdvancedSearchPresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val id: Long? = null,
        val studyReviewId: String? = null,
        val title: String? = null,
        val authors: String? = null,
        val venue: String? = null,
        val year: Int? = null,
        val selectionStatus: String? = null,
        val extractionStatus: String? = null,
        val score: Double? = null,
        val readingPriority: Int? = null,
        val page: Int = 0,
        val pageSize: Int = 20,
        val sort: String = "id,asc"
    )

    @Schema(name = "FindAllStudyReviewsByAdvancedSearchResponseModel")
    data class ResponseModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val studyReviews: List<StudyReviewDto>,
        val page: Int,
        val size: Int,
        val totalElements: Long,
        val totalPages: Int
    )
}
