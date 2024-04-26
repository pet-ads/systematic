package br.all.application.researcher

import java.util.UUID

interface CredentialsService {
    fun authenticatedUserCredentials(): ResponseModel?
    data class ResponseModel(val id: UUID, val name: String)
}