package br.all.application.user.create

import io.swagger.v3.oas.annotations.media.Schema

interface PostPasswordRecoveryTokenService {
    fun postPasswordRecovery(presenter: PostPasswordRecoveryTokenPresenter, request: RequestModel)

    data class RequestModel(
        val email: String,
    )

    @Schema(name = "PostPasswordRecoveryServiceResponseModel", description = "Response model for Password Recovery Password Service")
    class ResponseModel
}