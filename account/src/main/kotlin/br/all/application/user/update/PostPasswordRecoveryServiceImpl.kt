package br.all.application.user.update

import br.all.application.user.email.EmailMessage
import br.all.application.user.email.EmailService
import br.all.application.user.repository.UserAccountRepository
import br.all.application.user.usecase.GeneratePasswordResetTokenUseCase
import org.springframework.stereotype.Service

@Service
class PostPasswordRecoveryServiceImpl(
    private val repository: UserAccountRepository,
    private val generatePasswordResetTokenUseCase: GeneratePasswordResetTokenUseCase,
    private val emailService: EmailService,
) : PostPasswordRecoveryService {
    override fun postPasswordRecovery(
        presenter: PostPasswordRecoveryPresenter,
        request: PostPasswordRecoveryService.RequestModel
    ) {
        val userCredentials = repository.findByEmail(request.email)
        if (userCredentials != null) {
            val token = generatePasswordResetTokenUseCase.execute(userCredentials)
            val emailMessage = EmailMessage(userCredentials.email, "Password recovery for ${userCredentials.name}", "Testing.. ${token.id}", true)
            emailService.send(emailMessage)
            presenter.prepareSuccessView(PostPasswordRecoveryService.ResponseModel())
        }

        presenter.prepareSuccessView(PostPasswordRecoveryService.ResponseModel())
    }

}