package br.all.application.study.find.service

import br.all.application.study.find.presenter.FindAllStudyReviewsBySessionPresenter
import br.all.application.study.repository.StudyReviewDto
import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

interface FindAllStudyReviewsBySessionService {

    fun findAllBySearchSession(presenter: FindAllStudyReviewsBySessionPresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val searchSessionId: UUID,
        val page: Int = 0,
        val pageSize: Int = 20,
        val sort: String = "id,asc"
    )

    @Schema(name = "FindAllStudyReviewsBySessionResponseModel")
    data class ResponseModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val searchSessionId: UUID,
        val studyReviews: List<StudyReviewDto>,
        val page: Int,
        val size: Int,
        val totalElements: Long,
        val totalPages: Int
    )
}