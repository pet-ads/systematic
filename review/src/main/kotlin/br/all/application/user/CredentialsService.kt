package br.all.application.user

import br.all.domain.model.researcher.Researcher
import br.all.domain.model.researcher.ResearcherId
import br.all.domain.model.researcher.Role
import java.util.UUID

interface CredentialsService {

    fun loadCredentials(userId: UUID): ResponseModel?

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

