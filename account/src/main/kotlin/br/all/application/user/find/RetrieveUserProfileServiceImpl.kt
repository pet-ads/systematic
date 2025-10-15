package br.all.application.user.find

import br.all.application.user.find.RetrieveUserProfileService.RequestModel
import br.all.application.user.find.RetrieveUserProfileService.ResponseModel
import br.all.application.user.repository.UserAccountRepository

class RetrieveUserProfileServiceImpl(
    private val repository: UserAccountRepository
) : RetrieveUserProfileService {
    override fun retrieveData(
        presenter: RetrieveUserProfilePresenter,
        request: RequestModel
    ) {
        val userAccount = repository.loadFullUserAccountById(request.userId)
        if (userAccount == null) {
            presenter.prepareFailView(NoSuchElementException("User with id ${request.userId} doesn't exist!"))
            return
        }

        val profile = ResponseModel(
            userId = userAccount.id,
            name = userAccount.name,
            username = userAccount.username,
            email = userAccount.email,
            affiliation = userAccount.affiliation,
            country = userAccount.country,
            authorities = userAccount.authorities
        )

        presenter.prepareSuccessView(profile)
    }
}