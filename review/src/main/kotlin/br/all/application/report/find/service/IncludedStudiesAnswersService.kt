package br.all.application.report.find.service

import br.all.application.report.find.presenter.IncludedStudiesAnswersPresenter
import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

interface IncludedStudiesAnswersService {
    fun findAnswers(presenter: IncludedStudiesAnswersPresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val studyReviewId: Long
    )

    @Schema(name = "IncludedStudiesAnswersResponseModel")
    data class ResponseModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val studyReviewId: Long,
        val year: Int,
        val includedBy: Set<String>,
        val extractionQuestions: List<QuestionWithAnswer>,
        val robQuestions: List<QuestionWithAnswer>
    )

    data class QuestionWithAnswer(
        val questionId: UUID,
        val code: String,
        val type: String,
        val description: String,
        val answer: String?
    )
}