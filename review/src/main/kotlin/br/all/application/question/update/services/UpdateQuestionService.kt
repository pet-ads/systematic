package br.all.application.question.update.services

import br.all.application.question.create.CreateQuestionService.QuestionType
import br.all.application.question.update.presenter.UpdateQuestionPresenter
import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

interface UpdateQuestionService {
    fun update(presenter: UpdateQuestionPresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val questionId: UUID,
        val questionType: QuestionType,
        val code: String,
        val description: String,
        val scales: Map<String, Int>? = null,
        val higher: Int? = null,
        val lower: Int? = null,
        val options: List<String>? = null,
    )

    @Schema(name = "UpdateQuestionResponseModel")
    data class ResponseModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val questionId: UUID,
    )
}