package br.all.infrastructure.review

import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document("collaborator_token")
data class CollaboratorDocument(
    val researcherId: UUID,
    val systematicStudyId: UUID,
    val username: String,
    val email: String,
    val role: String,
)
