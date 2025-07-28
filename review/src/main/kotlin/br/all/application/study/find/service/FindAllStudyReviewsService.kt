package br.all.application.study.find.service

import br.all.application.study.find.presenter.FindAllStudyReviewsPresenter
import br.all.application.study.repository.StudyReviewDto
import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

interface FindAllStudyReviewsService {
    fun findAllFromReview(presenter: FindAllStudyReviewsPresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val page: Int = 0,
        val pageSize: Int = 20,
        val sort: String = "id,asc"
    )

    @Schema(name = "FindAllStudyReviewsServiceResponseModel")
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