package br.all.infrastructure.collaborator

import br.all.application.user.repository.TokenStatus
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import java.util.*

@Document("collaborator_token")
data class CollaboratorTokenDocument(
    @Id val id: UUID,

    var systematicStudyId: UUID,
    var researcherId: UUID,
    var email: String,
    var username: String,
    var status: TokenStatus,
    var createdAt: LocalDateTime,
    var expiration: LocalDateTime,
)
