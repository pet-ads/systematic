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
        val userProfile = userAccountRepository.loadUserProfileById(request.userId)
        if (userProfile == null) {
            presenter.prepareFailView(NoSuchElementException("User with id ${request.userId} doesn't exist!"))
            return
        }

        val userCredentials = userAccountRepository.loadCredentialsById(request.userId)
        if (userCredentials == null) {
            presenter.prepareFailView(NoSuchElementException("Account credentials with id ${request.userId} doesn't exist!"))
            return
        }


        val profile = ResponseModel(
            userId = userProfile.id,
            username = userCredentials.username,
            email = userProfile.email,
            affiliation = userProfile.affiliation,
            country = userProfile.country,
            authorities = userCredentials.authorities
        )

        presenter.prepareSuccessView(profile)
    }
}