package br.all.application.review.create

import java.util.*

interface CreateSystematicStudyService {
    fun create(presenter: CreateSystematicStudyPresenter, researcherId: UUID, request: RequestModel)

    data class RequestModel(
        val title : String,
        val description : String,
        val collaborators : Set<UUID>,
    )

    data class ResponseModel(
        val researcherId: UUID,
        val systematicStudyId: UUID,
    )
}
