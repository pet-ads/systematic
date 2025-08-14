package br.all.application.user

import br.all.domain.shared.user.Researcher
import br.all.domain.shared.user.ResearcherId
import br.all.domain.shared.user.Role
import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

interface CredentialsService {

    fun loadCredentials(userId: UUID): ResponseModel?

    @Schema(name = "CredentialsServiceResponseModel")
    data class ResponseModel(val id: UUID, val username: String, val roles: Set<String>){
        fun toUser() : Researcher {
            val researcherId = ResearcherId(id)
            val userRoles = roles.toMutableSet()
                .map { if (it == "USER") "COLLABORATOR" else it }
                .map { Role.valueOf(it) }
                .toSet()

            return Researcher(researcherId, username, userRoles)
        }
    }
}

