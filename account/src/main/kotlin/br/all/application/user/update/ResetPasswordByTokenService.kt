package br.all.application.user.update

import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

interface ResetPasswordByTokenService {
    fun execute(presenter: ResetPasswordByTokenPresenter, requestModel: ResetPasswordByTokenService.RequestModel)

    data class RequestModel(
        val token: UUID,
        val newPassword: String
    )

    @Schema(name = "ResetPasswordByTokenServiceResponseModel", description = "Response model for Reset Password By Token Service")
    class ResponseModel
}