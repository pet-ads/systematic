package br.all.application.question.findAll

import br.all.application.question.repository.QuestionDto
import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

interface FindAllBySystematicStudyIdService {
    fun findAllBySystematicStudyId(presenter: FindAllBySystematicStudyIdPresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val context: String? = null
    )


    @Schema(name = "FindAllBySystematicStudyIdServiceResponseModel")
    data class ResponseModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val questions: List<QuestionDto>
    )
}