package br.all.application.protocol.find

import br.all.application.protocol.repository.ProtocolDto
import java.util.*

interface FindOneProtocolService {
    fun findById(presenter: FindOneProtocolPresenter, request: RequestModel)

    data class RequestModel(
        val researcherId: UUID,
        val systematicStudyId: UUID,
    )

    data class ResponseModel(
        val researcherId: UUID,
        val systematicStudyId: UUID,
        val content: ProtocolDto,
    )
}