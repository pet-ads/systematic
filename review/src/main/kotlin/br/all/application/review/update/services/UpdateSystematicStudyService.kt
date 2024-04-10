package br.all.application.review.update.services

import br.all.application.review.update.presenter.UpdateSystematicStudyPresenter
import java.util.*

interface UpdateSystematicStudyService {
    fun update(presenter: UpdateSystematicStudyPresenter, request: RequestModel)

    data class RequestModel(
        val researcher: UUID,
        val systematicStudy: UUID,
        val title: String?,
        val description: String?,
    )

    data class ResponseModel(
        val researcherId: UUID,
        val systematicStudy: UUID,
    )
}
