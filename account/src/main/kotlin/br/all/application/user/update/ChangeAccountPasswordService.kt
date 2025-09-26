package br.all.application.user.update

import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

interface ChangeAccountPasswordService {
    fun changePassword(presenter: ChangeAccountPasswordPresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID,
        val oldPassword: String,
        val newPassword: String,
        val confirmPassword: String
    )

    @Schema(name = "ChangeAccountPasswordServiceResponseModel", description = "Response model for Change Account Password Service")
    data class ResponseModel(
        val userId: UUID
    )
}