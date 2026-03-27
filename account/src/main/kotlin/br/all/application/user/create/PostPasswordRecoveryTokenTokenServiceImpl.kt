package br.all.application.user.create

import br.all.application.user.email.EmailMessage
import br.all.application.user.email.EmailService
import br.all.application.user.repository.UserAccountRepository
import br.all.application.user.usecase.GeneratePasswordRecoveryTokenUseCase
import org.springframework.stereotype.Service

@Service
class PostPasswordRecoveryTokenTokenServiceImpl(
    private val repository: UserAccountRepository,
    private val generatePasswordRecoveryTokenUseCase: GeneratePasswordRecoveryTokenUseCase,
    private val emailService: EmailService,
) : PostPasswordRecoveryTokenService {
    override fun postPasswordRecovery(
        presenter: PostPasswordRecoveryTokenPresenter,
        request: PostPasswordRecoveryTokenService.RequestModel
    ) {
        val userCredentials = repository.findByEmail(request.email)
        if (userCredentials != null) {
            val token = generatePasswordRecoveryTokenUseCase.execute(userCredentials)
            val emailMessage = EmailMessage(userCredentials.email, "Password recovery for ${userCredentials.name}", "Testing.. ${token.id}", true)
            emailService.send(emailMessage)
            presenter.prepareSuccessView(PostPasswordRecoveryTokenService.ResponseModel())
        }

        presenter.prepareSuccessView(PostPasswordRecoveryTokenService.ResponseModel())
    }

}