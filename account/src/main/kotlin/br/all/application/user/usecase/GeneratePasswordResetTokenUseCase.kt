package br.all.application.user.usecase

import br.all.application.user.email.EmailService
import br.all.application.user.repository.UserAccountRepository
import br.all.application.user.repository.UserPasswordTokenDto
import br.all.application.user.repository.UserPasswordTokenRepository
import br.all.infrastructure.user.TokenStatus
import java.time.LocalDateTime.now
import java.util.UUID

class GeneratePasswordResetTokenUseCase(
    private val userRepository: UserAccountRepository,
    private val tokenRepository: UserPasswordTokenRepository,
    private val emailService: EmailService
) {

    fun execute(email: String) {
        val user = userRepository.findByEmail(email)
            ?: return

        val existingToken = tokenRepository.findByEmail(email)

        if (existingToken != null && existingToken.expiration.isAfter(now())) {
            val updated = existingToken.copy(
                expiration = now().plusHours(1)
            )

            tokenRepository.update(updated)
            //emailService.send(user, existingToken.token)
            return
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

        tokenRepository.save(token)

        //emailService.send(user, newToken)
    }
}