package br.all.application.search.repository

import br.all.domain.model.researcher.ResearcherId
import java.time.LocalDateTime
import java.util.*

data class SearchSessionDto(
    val id: UUID,
    val systematicStudyId: UUID,
    val researcherId: UUID,
    val searchString: String,
    val additionalInfo: String?,
    val timestamp: LocalDateTime,
    val source: String
)