package br.all.application.study.update.interfaces

import java.util.*

interface AnswerRiskOfBiasQuestionService {
    fun answer(presenter: AnswerRiskOfBiasQuestionPresenter, request: RequestModel<*>)

    data class RequestModel<T>(
        val researcherId: UUID,
        val systematicStudyId: UUID,
        val studyReviewId: Long,
        val questionId: UUID,
        val type: String,
        val answer: T,
    )

    data class LabelDto(
        val name: String,
        val value: Int,
    )

    data class ResponseModel(
        val researcherId: UUID,
        val systematicStudyId: UUID,
        val studyReviewId: Long,
    )
}