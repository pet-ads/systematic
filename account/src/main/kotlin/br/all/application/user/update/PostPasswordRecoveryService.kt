package br.all.application.user.update

import io.swagger.v3.oas.annotations.media.Schema

interface PostPasswordRecoveryService {
    fun postPasswordRecovery(presenter: PostPasswordRecoveryPresenter, request: RequestModel)

    data class RequestModel(
        val email: String,
    )

    @Schema(name = "PostPasswordRecoveryServiceResponseModel", description = "Response model for Password Recovery Password Service")
    class ResponseModel
}