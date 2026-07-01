package br.all.application.user

import br.all.application.user.repository.UserSummaryDto
import java.util.UUID

interface SearchResearchesService {
    fun searchUsers(prefix: String): List<UserSummaryDto>

    data class ResponseModel(val id: UUID, val username: String, val email: String)
}

