package br.all.application.review.repository

import br.all.application.review.create.CreateSystematicStudyService.RequestModel
import br.all.domain.model.review.CollaboratorToken
import br.all.domain.model.review.CollaboratorTokenId
import br.all.domain.shared.user.ResearcherId
import br.all.domain.model.review.SystematicStudy
import br.all.domain.model.review.SystematicStudyId
import org.springframework.http.ResponseEntity.created
import java.util.*

fun CollaboratorToken.toDto() = CollaboratorTokenDto(
    id.value(),
    systematicStudyId,
    researcherId,
    status,
    createdAt,
    expiration,
)

fun CollaboratorToken.Companion.fromDto(dto: CollaboratorTokenDto) = CollaboratorToken(
    CollaboratorTokenId(dto.id),
    dto.systematicStudyId,
    dto.researcherId,
    dto.status,
    dto.createdAt,
    dto.expiration,
)
