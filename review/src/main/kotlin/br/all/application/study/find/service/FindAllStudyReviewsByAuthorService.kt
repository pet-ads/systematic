package br.all.application.study.find.service

import br.all.application.study.create.CreateStudyReviewService
import br.all.application.study.find.presenter.FindAllStudyReviewsByAuthorPresenter
import br.all.application.study.repository.StudyReviewDto
import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

interface FindAllStudyReviewsByAuthorService {

    fun findAllByAuthor(presenter: FindAllStudyReviewsByAuthorPresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val author: String
    )
    @Schema(name = "FindAllStudyReviewsByAuthorResponseModel")
    data class ResponseModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val studyReviews: List<StudyReviewDto>
    )
}