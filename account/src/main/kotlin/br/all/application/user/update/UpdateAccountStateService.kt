package br.all.application.user.update

import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

interface UpdateAccountStateService {
    fun updateState(presenter: UpdateAccountStatePresenter, requestModel: RequestModel)

    data class RequestModel(
        val token: UUID
    )

    @Schema(name = "UpdateAccountStateServiceResponseModel", description = "Response model for Update Account State Service")
    data class ResponseModel(
        val message: String
    )
}