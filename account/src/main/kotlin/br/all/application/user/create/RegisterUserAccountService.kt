package br.all.application.user.create

import java.util.UUID

interface RegisterUserAccountService {

    fun register(presenter: RegisterUserAccountPresenter, request: RequestModel)

    data class RequestModel(
        val username: String,
        val password: String,
        val email: String,
        val country: String,
        val affiliation: String,
    )

    data class ResponseModel(
        val id: UUID,
        val username: String,
        val email: String,
    )
}

