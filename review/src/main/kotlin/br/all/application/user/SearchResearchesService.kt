package br.all.application.user

import java.util.UUID

interface SearchResearchesService {
    fun searchUsers(prefix: String): List<ResponseModel?>

    data class ResponseModel(val id: UUID, val username: String, val email: String)
}

