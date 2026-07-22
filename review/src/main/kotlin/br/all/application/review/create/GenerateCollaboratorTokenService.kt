package br.all.application.review.create

import br.all.application.review.repository.CollaboratorTokenDto
import br.all.application.review.repository.CollaboratorTokenRepository
import br.all.application.user.repository.TokenStatus
import br.all.domain.shared.user.Email
import br.all.domain.shared.user.Username
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

@Service
class GenerateCollaboratorTokenService (
    private val repository: CollaboratorTokenRepository
){
    fun generateCollaboratorToken(systematicStudyId: UUID, researcherId: UUID, email: String, username: String): UUID {
        val now = LocalDateTime.now()

        val dto = CollaboratorTokenDto(
            id = UUID.randomUUID(),
            systematicStudyId = systematicStudyId,
            researcherId = researcherId,
            email = email,
            username = username,
            status = TokenStatus.PENDENTE,
            createdAt = now,
            expiration = now.plusDays(3)
        )

        repository.saveOrUpdate(dto)

        return dto.id
    }
}