package br.all.application.report.find.service

import br.all.application.question.repository.QuestionDto
import br.all.application.report.find.presenter.FindAnswerPresenter
import java.util.*

interface FindAnswerService {
    fun find(presenter: FindAnswerPresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val questionId: UUID,
    )

    data class ResponseModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val question: QuestionDto,
        val answer: Map<String, List<Long>>
    )
}