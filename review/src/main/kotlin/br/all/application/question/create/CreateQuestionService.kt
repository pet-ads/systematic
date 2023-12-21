package br.all.application.question.create

import br.all.domain.model.protocol.ProtocolId
import br.all.domain.model.question.QuestionId
import br.all.domain.shared.ddd.Notification
import java.util.*

interface CreateQuestionService {
    fun createRiskOfBiasQuestion(presenter: CreateQuestionPresenter, request: RequestModel)
    fun createExtractionQuestion(presenter: CreateQuestionPresenter, request: RequestModel)

    data class RequestModel(
        val researcherId: UUID,
        val systematicStudyId: UUID,
        val protocolId: ProtocolId,
        val questionType: String,
        val code: String,
        val description: String,
        val scales: Map<String, Int>,
        val higher: Int,
        val lower: Int,
        val options: List<String>,
    ) {
        val questionId: QuestionId = QuestionId(UUID.randomUUID())
    }

    open class ResponseModel(
        val researcherId: UUID,
        val systematicStudyId: UUID,
        val protocolId: ProtocolId,
        val questionId: QuestionId
    )
}