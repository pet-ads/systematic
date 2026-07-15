package br.all.infrastructure.review

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document("collaborator")
data class CollaboratorDocument(
    @Id val id: String,
    val researcherId: UUID,
    val systematicStudyId: UUID,
    val username: String,
    val email: String,
    val role: String,
) {
    companion object {
        fun buildId(researcherId: UUID, systematicStudyId: UUID) = "$researcherId:$systematicStudyId"
    }
}