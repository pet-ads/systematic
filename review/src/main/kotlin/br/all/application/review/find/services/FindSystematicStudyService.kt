package br.all.application.review.find.services

import br.all.application.review.find.presenter.FindSystematicStudyPresenter
import br.all.application.review.repository.SystematicStudyDto
import java.util.*

interface FindSystematicStudyService {
    fun findById(presenter: FindSystematicStudyPresenter, request: RequestModel)

    data class RequestModel(
        val researcher: UUID,
        val systematicStudy: UUID,
    )

    data class ResponseModel(
        val researcherId: UUID,
        val systematicStudyId: UUID,
        val content: SystematicStudyDto,
    )
}
