package br.all.application.review.find.services

import br.all.application.review.find.presenter.FindSystematicStudyPresenter
import br.all.application.review.repository.SystematicStudyDto
import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

interface FindSystematicStudyService {
    fun findById(presenter: FindSystematicStudyPresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID,
        val systematicStudy: UUID,
    )
    @Schema(name = "FindSystematicStudyServiceResponseModel")
    data class ResponseModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val content: SystematicStudyDto,
    )
}
