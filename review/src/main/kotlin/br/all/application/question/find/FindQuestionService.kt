package br.all.application.question.find

import br.all.application.question.repository.QuestionDto
import br.all.domain.model.question.QuestionId
import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

interface FindQuestionService {
    fun findOne(presenter: FindQuestionPresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val questionId: UUID,
        val context: String? = null
    )

    @Schema(name = "FindQuestionServiceResponseModel", description = "Response model for Find Question Service")
    data class ResponseModel(
        val userId: UUID,
        val content: QuestionDto
    )
}