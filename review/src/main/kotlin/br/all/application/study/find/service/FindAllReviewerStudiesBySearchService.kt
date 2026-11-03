package br.all.application.study.find.service

import br.all.application.study.find.presenter.FindAllStudyReviewsByAdvancedSearchPresenter
import br.all.domain.model.study.StudyReviewStage
import java.util.UUID

interface FindAllReviewerStudiesBySearchService {
    fun findAllBySearch(presenter: FindAllStudyReviewsByAdvancedSearchPresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val reviewerId: UUID,
        val stage: StudyReviewStage? = null,
        val id: Long? = null,
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
}