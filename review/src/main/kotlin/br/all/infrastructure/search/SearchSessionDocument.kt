package br.all.infrastructure.search

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import java.util.*

@Document("search_session")
data class SearchSessionDocument(
    @Id
    val id: UUID,
    val systematicStudyId: UUID,
    val searchString: String,
    val additionalInfo: String?,
    val timestamp: LocalDateTime,
    val source: String
)
