package br.all.application.study.find.service

import br.all.application.study.find.presenter.FindAllStudyReviewsByReviewerPresenter
import br.all.application.study.repository.StudyReviewDto
import br.all.domain.model.study.StudyReviewStage
import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

interface FindAllStudyReviewsByReviewerService {
    fun findAllByReviewer(presenter: FindAllStudyReviewsByReviewerPresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val reviewerId: UUID,
        val stage: StudyReviewStage? = null,
        val page: Int = 0,
        val pageSize: Int = 20,
        val sort: String = "id,asc"
    )

    @Schema(name = "FindAllStudyReviewsByReviewerResponseModel")
    data class ResponseModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val reviewerId: UUID,
        val stage: StudyReviewStage?,
        val studyReviews: List<StudyReviewDto>,
        val page: Int,
        val size: Int,
        val totalElements: Long,
        val totalPages: Int
    )
}