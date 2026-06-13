package br.all.application.review.repository

import java.util.*

data class CollaboratorDto(
    val researcherId: UUID,
    var systematicStudyId: UUID,
    val username: String,
    val email: String,
    var role: String,
)

