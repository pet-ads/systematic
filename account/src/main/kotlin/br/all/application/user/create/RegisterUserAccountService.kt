package br.all.application.user.create

import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

interface RegisterUserAccountService {

    fun register(presenter: RegisterUserAccountPresenter, request: RequestModel)

    data class RequestModel(
        val name: String,
        val username: String,
        val password: String,
        val email: String,
        val country: String,
        val affiliation: String,
    )

    @Schema(name = "RegisterUserAccountResponseModel", description = "Response model for Register User Account Service")
    data class ResponseModel(
        val id: UUID,
        val username: String,
        val email: String,
    )
}

