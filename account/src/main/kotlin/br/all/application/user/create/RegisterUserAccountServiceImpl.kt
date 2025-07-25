package br.all.application.user.create

import br.all.application.shared.UniquenessViolationException
import br.all.application.user.create.RegisterUserAccountService.RequestModel
import br.all.application.user.create.RegisterUserAccountService.ResponseModel
import br.all.application.user.repository.UserAccountRepository
import br.all.application.user.repository.toDto
import br.all.domain.shared.user.Email
import br.all.domain.user.*

class RegisterUserAccountServiceImpl(private val repository: UserAccountRepository) : RegisterUserAccountService {

    override fun register(presenter: RegisterUserAccountPresenter, request: RequestModel) {
        if (repository.existsByEmail(request.email)) {
            presenter.prepareFailView(UniquenessViolationException("The email ${request.email} is already registered."))
        }
        if (repository.existsByUsername(request.username)) {
            presenter.prepareFailView(UniquenessViolationException("The username ${request.username} is already registered."))
        }
        if (presenter.isDone()) return

        val userAccountId = UserAccountId()
        val username = Username(request.username)
        val email = Email(request.email)
        val country = Text(request.country)

        val userAccount = UserAccount(
            id = userAccountId,
            username = username,
            password = request.password,
            email = email,
            country = country,
            affiliation = request.affiliation,
            authorities = setOf(Authority.USER)
        )

        repository.save(userAccount.toDto())
        val responseModel = ResponseModel(userAccountId.value, request.username, request.email)
        presenter.prepareSuccessView(responseModel)
    }
}