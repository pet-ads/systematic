package br.all.application.researcher

import br.all.domain.model.researcher.Researcher
import br.all.domain.model.researcher.ResearcherId
import java.util.UUID

interface CredentialsService {
    fun loadCredentials(userId: UUID): ResponseModel?
    data class ResponseModel(val id: UUID, val username: String){
        fun toUser() = Researcher(ResearcherId(id), username, emptySet())
    }
}

