package br.all.application.question.update.services

import br.all.application.question.create.CreateQuestionService.QuestionType
import br.all.application.question.update.presenter.UpdateQuestionPresenter
import java.util.*

interface UpdateQuestionService {
    fun update(presenter: UpdateQuestionPresenter, request: RequestModel)

    data class RequestModel(
        val researcherId: UUID,
        val systematicStudyId: UUID,
        val questionType: QuestionType,
        val code: String,
        val description: String,
        val scales: Map<String, Int>? = null,
        val higher: Int? = null,
        val lower: Int? = null,
        val options: List<String>? = null,
    )

    data class ResponseModel(
        val researcherId: UUID,
        val systematicStudyId: UUID,
        val questionId: UUID,
    )
}