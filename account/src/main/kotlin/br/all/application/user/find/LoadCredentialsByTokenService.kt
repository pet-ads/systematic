package br.all.application.user.find

import java.util.*

interface LoadCredentialsByTokenService {

    fun loadByToken(refreshToken: String): ResponseModel

    data class ResponseModel(
        val id: UUID,
        val username: String,
    )
}

