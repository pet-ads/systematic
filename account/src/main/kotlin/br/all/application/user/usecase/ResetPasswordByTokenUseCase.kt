package br.all.application.user.usecase

import br.all.application.user.repository.TokenStatus
import br.all.application.user.repository.UserPasswordTokenRepository
import br.all.application.user.update.UpdatePasswordService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime.now

@Service
class ResetPasswordByTokenUseCase(
    private val tokenRepository: UserPasswordTokenRepository,
    private val updatePasswordService: UpdatePasswordService
) {

    @Transactional
    fun execute(token: String, newPassword: String) {
        val passwordToken = tokenRepository.findByToken(token)
            ?: throw IllegalArgumentException("Invalid token")

        if (passwordToken.status == TokenStatus.CONCLUIDO) {
            throw IllegalAccessException("Token already used")
        }

        if (passwordToken.expiration.isBefore(now())) {
            throw IllegalAccessException("Expired token")
        }

        updatePasswordService.update(
            passwordToken.userId,
            newPassword
        )

        tokenRepository.update(
            passwordToken.copy(
                status = TokenStatus.CONCLUIDO
            )
        )
    }
}