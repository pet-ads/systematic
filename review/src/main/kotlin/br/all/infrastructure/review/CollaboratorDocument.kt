package br.all.infrastructure.review

import br.all.application.user.repository.TokenStatus
import br.all.domain.shared.user.Email
import br.all.domain.shared.user.Role
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import java.util.*

@Document("collaborator_token")
data class CollaboratorDocument(
    val systematicStudyId: UUID,
    val researcherId: UUID,
    val username: String,
    val email: String,
    val role: String,
)
