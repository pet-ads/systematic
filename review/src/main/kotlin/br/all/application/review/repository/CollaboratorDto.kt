package br.all.application.review.repository

import br.all.domain.shared.user.Email
import br.all.domain.shared.user.Role
import java.util.*

data class CollaboratorDto(
    val id: UUID,
    var systematicStudyId: UUID,
    val username: String,
    val email: String,
    var role: String,
)
