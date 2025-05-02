package br.all.application.review.create

import java.util.*

interface CreateSystematicStudyService {
    fun create(presenter: CreateSystematicStudyPresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID,
        val title : String,
        val description : String,
    )

    data class ResponseModel(
        val userId: UUID,
        val systematicStudyId: UUID,
    )
}
