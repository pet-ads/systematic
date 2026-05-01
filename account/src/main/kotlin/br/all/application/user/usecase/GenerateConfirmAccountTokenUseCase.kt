package br.all.application.user.usecase

import br.all.application.user.repository.ConfirmAccountTokenDto
import br.all.application.user.repository.ConfirmAccountTokenRepository
import br.all.application.user.repository.TokenStatus
import org.springframework.stereotype.Service
import java.util.UUID
import java.time.LocalDateTime.now

@Service
class GenerateConfirmAccountTokenUseCase (
    private val tokenRepository: ConfirmAccountTokenRepository,
) {

    fun execute(userId: UUID): UUID {
        val now = now()

        val token = tokenRepository.findByUserId(userId)
            ?.takeIf { it.expiration.isAfter(now) }?.copy(expiration = now.plusHours(1))
            ?: ConfirmAccountTokenDto(
                token = UUID.randomUUID(),
                userId = userId,
                status = TokenStatus.PENDENTE,
                createdAt = now,
                expiration = now.plusDays(2),
            )

        return tokenRepository.save(token).token
    }
}