package br.all.application.review.update.services

import br.all.application.review.update.presenter.UpdateSystematicStudyPresenter
import java.util.*

interface UpdateSystematicStudyService {
    fun update(presenter: UpdateSystematicStudyPresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID,
        val systematicStudy: UUID,
        val title: String?,
        val description: String?,
        val objectives: String?
    )

    data class ResponseModel(
        val userId: UUID,
        val systematicStudy: UUID,
    )
}
