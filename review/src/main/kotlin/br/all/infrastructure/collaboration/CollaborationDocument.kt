package br.all.infrastructure.collaboration

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document("collaboration")
data class CollaborationDocument(
    @Id val id: UUID,
    val systematicStudyId: UUID,
    val userId: UUID,
    val status: String,
    val permissions: List<String>
)
