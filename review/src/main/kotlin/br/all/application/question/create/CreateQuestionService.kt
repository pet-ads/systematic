package br.all.application.question.create

import java.util.*

interface CreateQuestionService {
    fun create(presenter: CreateQuestionPresenter, request: RequestModel)

    data class RequestModel(
        val researcherId: UUID,
        val systematicStudyId: UUID,
        val protocolId: UUID,
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
        val protocolId: UUID,
        val questionId: UUID,
    )
}