package br.all.application.user.usecase

import br.all.application.user.repository.UserAccountDto
import br.all.application.user.repository.UserPasswordTokenDto
import br.all.application.user.repository.UserPasswordTokenRepository
import br.all.infrastructure.user.TokenStatus
import org.springframework.stereotype.Service
import java.time.LocalDateTime.now
import java.util.UUID

@Service
class GeneratePasswordResetTokenUseCase(
    private val tokenRepository: UserPasswordTokenRepository,
) {

    fun execute(user: UserAccountDto): UserPasswordTokenDto {
        val existingToken = tokenRepository.findByEmail(user.email)

        if (existingToken != null && existingToken.expiration.isAfter(now())) {
            val updated = existingToken.copy(
                expiration = now().plusHours(1)
            )

            return tokenRepository.save(updated)
        }

        val newToken = UUID.randomUUID().toString()

        val token = UserPasswordTokenDto(
            id = UUID.randomUUID(),
            userId = user.id,
            email = user.email,
            token = newToken,
            status = TokenStatus.PENDENTE,
            createdAt = now(),
            expiration = now().plusHours(1),
            language = user.country
        )

        return tokenRepository.save(token)
    }
}