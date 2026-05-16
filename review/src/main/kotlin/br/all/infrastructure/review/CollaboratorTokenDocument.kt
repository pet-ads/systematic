package br.all.infrastructure.review

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
    var status: TokenStatus,
    var createdAt: LocalDateTime,
    var expiration: LocalDateTime,
)
