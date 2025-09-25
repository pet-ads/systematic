package br.all.application.user.update

import br.all.application.user.repository.UserAccountRepository
import br.all.domain.shared.service.PasswordEncoderPort

class ChangeAccountPasswordServiceImpl(
    private val repository: UserAccountRepository,
    private val encoder: PasswordEncoderPort
) : ChangeAccountPasswordService {
    override fun changePassword(
        presenter: ChangeAccountPasswordPresenter,
        request: ChangeAccountPasswordService.RequestModel
    ) {
        val userCredentials = repository.loadCredentialsById(request.userId)
        if (userCredentials == null) {
            presenter.prepareFailView(NoSuchElementException("User with id ${request.userId} doesn't exist!"))
            return
        }

        if (!encoder.matches(request.oldPassword, userCredentials.password)) {
            presenter.prepareFailView(IllegalArgumentException("Invalid old password provided!"))
            return
        }

        if (request.newPassword != request.confirmPassword) {
            presenter.prepareFailView(IllegalArgumentException("Confirm password does not match new password!"))
            return
        }

        if (encoder.matches(request.newPassword, userCredentials.password)) {
            presenter.prepareFailView(IllegalArgumentException("New password cannot be the same as the old password!"))
            return
        }

        val newHashedPassword = encoder.encode(request.newPassword)

        try {
            repository.updatePassword(request.userId, newHashedPassword)
            presenter.prepareSuccessView(ChangeAccountPasswordService.ResponseModel(
                request.userId,
            ))
        } catch (e: Exception) {
            presenter.prepareFailView(e)
        }
    }

}