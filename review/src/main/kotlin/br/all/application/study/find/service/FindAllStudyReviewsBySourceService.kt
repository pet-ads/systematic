package br.all.application.study.find.service

import br.all.application.study.find.presenter.FindAllStudyReviewsBySourcePresenter
import br.all.application.study.repository.StudyReviewDto
import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

interface FindAllStudyReviewsBySourceService {
    fun findAllFromSearchSession(presenter: FindAllStudyReviewsBySourcePresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val searchSource: String,
    )
    @Schema(name = "FindAllStudyReviewsBySourceResponseModel")
    data class ResponseModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val searchSource: String,
        val studyReviews: List<StudyReviewDto>
    )
}