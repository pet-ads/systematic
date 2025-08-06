package br.all.application.user.find

import br.all.application.user.find.RetrieveUserProfileService.RequestModel
import br.all.application.user.find.RetrieveUserProfileService.ResponseModel
import br.all.application.user.repository.UserAccountRepository

class RetrieveUserProfileServiceImpl(
    private val userAccountRepository: UserAccountRepository
) : RetrieveUserProfileService {
    override fun retrieveData(
        presenter: RetrieveUserProfilePresenter,
        request: RequestModel
    ) {
        val user = userAccountRepository.loadUserProfileById(request.userId)
        if (user == null) {
            presenter.prepareFailView(NoSuchElementException("User with id ${request.userId} doesn't exist!"))
            return
        }

        val profile = ResponseModel(
            userId = user.id,
            username = user.username,
            email = user.email,
            affiliation = user.affiliation,
            country = user.country
        )

        presenter.prepareSuccessView(profile)
    }
}