package br.all.application.question.create

import java.util.*

interface CreateQuestionService {
    fun create(presenter: CreateQuestionPresenter, request: RequestModel)

    enum class QuestionType{TEXTUAL, PICK_LIST, NUMBERED_SCALE, LABELED_SCALE, PICK_MANY}

    data class RequestModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val questionContext: String,
        val questionType: QuestionType,
        val code: String,
        val description: String,
        val scales: Map<String, Int>? = null,
        val higher: Int? = null,
        val lower: Int? = null,
        val options: List<String>? = null,
    )

    data class ResponseModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val questionId: UUID,
    )
}