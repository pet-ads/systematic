package br.all.infrastructure.review

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document("systematic_study")
data class SystematicStudyDocument(
    @Id val id: UUID,
    val title: String,
    val description: String,
    val owner: UUID,
    val collaborators: Set<UUID> = emptySet(),
)
