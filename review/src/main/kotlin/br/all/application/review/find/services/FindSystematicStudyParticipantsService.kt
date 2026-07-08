package br.all.application.review.find.services

import br.all.application.review.find.presenter.FindSystematicStudyParticipantsPresenter
import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

interface FindSystematicStudyParticipantsService {

    fun findParticipants(
        presenter: FindSystematicStudyParticipantsPresenter,
        request: RequestModel
    )

    data class RequestModel(
        val userId: UUID,
        val systematicStudyId: UUID,
    )

    @Schema(name = "FindSystematicStudyParticipantsResponseModel")
    data class ResponseModel(
        val systematicStudyId: UUID,
        val participants: Set<UUID>,
    )
}