package br.all.application.question.findAll

import br.all.application.question.repository.QuestionDto
import java.util.UUID

interface FindAllBySystematicStudyIdService {
    fun findAllBySystematicStudyId(presenter: FindAllBySystematicStudyIdPresenter, request: RequestModel)

    data class RequestModel(
        val researcherId: UUID,
        val systematicStudyId: UUID
    )

    data class ResponseModel(
        val researcherId: UUID,
        val systematicStudyId: UUID,
        val questions: List<QuestionDto>
    )
}