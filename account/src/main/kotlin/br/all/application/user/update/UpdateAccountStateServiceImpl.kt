package br.all.application.user.update

import br.all.application.user.repository.ConfirmAccountTokenRepository
import br.all.application.user.repository.TokenStatus
import br.all.application.user.repository.UserAccountRepository
import org.springframework.stereotype.Service
import br.all.application.user.update.UpdateAccountStateService.RequestModel
import br.all.application.user.update.UpdateAccountStateService.ResponseModel
import jakarta.transaction.Transactional
import java.time.LocalDateTime

@Service
class UpdateAccountStateServiceImpl (
    private val confirmAccountTokenRepository: ConfirmAccountTokenRepository,
    private val userAccountRepository: UserAccountRepository
): UpdateAccountStateService {

    @Transactional
    override fun updateState(
        presenter: UpdateAccountStatePresenter,
        requestModel: RequestModel
    ) {
        val confirmToken = confirmAccountTokenRepository.findById(requestModel.token)
            ?: return presenter.prepareFailView(IllegalArgumentException("Invalid token"))

        if (confirmToken.status == TokenStatus.CONCLUIDO) {
            presenter.prepareFailView(IllegalArgumentException("Token already used"))
            return
        }

        if (confirmToken.expiration.isBefore(LocalDateTime.now())) {
            presenter.prepareFailView(IllegalArgumentException("Expired token"))
            return
        }

        if (presenter.isDone()) return

        userAccountRepository.updateIsEnabled(confirmToken.userId, true)

        confirmAccountTokenRepository.update(
            confirmToken.copy(
                status = TokenStatus.CONCLUIDO
            )
        )

        presenter.prepareSuccessView(ResponseModel("Account confirmed successfully!"))
    }
}