package br.all.application.study.find.service

import br.all.application.study.find.presenter.FindStudyReviewPresenter
import br.all.application.study.repository.StudyReviewDto
import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

interface FindStudyReviewService {
    fun findOne(presenter: FindStudyReviewPresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val studyReviewId: Long
    )

    @Schema(name = "FindStudyReviewServiceResponseModel")
    data class ResponseModel(
        val userId: UUID,
        val content: StudyReviewDto
    )
}