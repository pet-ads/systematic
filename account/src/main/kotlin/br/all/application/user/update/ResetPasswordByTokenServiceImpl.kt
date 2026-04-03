package br.all.application.user.update

import br.all.application.user.repository.TokenStatus
import br.all.application.user.repository.UserPasswordTokenRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class ResetPasswordByTokenServiceImpl(
    private val tokenRepository: UserPasswordTokenRepository,
    private val updatePasswordService: UpdatePasswordService
): ResetPasswordByTokenService {

    @Transactional
    override fun execute(presenter: ResetPasswordByTokenPresenter, requestModel: ResetPasswordByTokenService.RequestModel) {
        val passwordToken = tokenRepository.findByToken(requestModel.token)
            ?: return presenter.prepareFailView(IllegalArgumentException("Invalid token"))

        if (passwordToken.status == TokenStatus.CONCLUIDO) {
            presenter.prepareFailView(IllegalArgumentException("Token already used"))
            return
        }

        if (passwordToken.expiration.isBefore(LocalDateTime.now())) {
            presenter.prepareFailView(IllegalArgumentException("Expired token"))
            return
        }

        if (presenter.isDone()) return

        updatePasswordService.update(
            passwordToken.userId,
            requestModel.newPassword
        )

        tokenRepository.update(
            passwordToken.copy(
                status = TokenStatus.CONCLUIDO
            )
        )

        presenter.prepareSuccessView(ResetPasswordByTokenService.ResponseModel())
    }
}