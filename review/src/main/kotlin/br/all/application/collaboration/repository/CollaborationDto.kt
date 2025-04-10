package br.all.application.collaboration.repository

import java.util.*

class CollaborationDto (
    val id: UUID,
    val systematicStudyId: UUID,
    val userId: UUID,
    val status: String,
    val permissions: Set<String>
)