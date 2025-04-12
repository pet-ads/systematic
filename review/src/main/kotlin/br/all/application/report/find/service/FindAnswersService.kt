package br.all.application.report.find.service

import br.all.application.report.find.presenter.FindAnswersPresenter
import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

interface FindAnswersService {
    fun findAnswers(presenter: FindAnswersPresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val studyReviewId: Long,
        val questionId: UUID
    )

    @Schema(name = "FindAnswersResponseModel")
    data class ResponseModel<T>(
        val userId: UUID,
        val systematicStudyId: UUID,
        val studyReviewId: Long,
        val questionId: UUID,
        val code: String,
        val description: String,
        val type: String,
        val answer: T
    )
}