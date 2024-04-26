package br.all.application.user.find

import java.util.UUID

interface LoadAccountCredentialsService {
    fun loadByUsername(username: String): ResponseModel

    data class ResponseModel(
        val id: UUID,
        val username: String,
        val password: String
    )
}