package br.all.application.user.update

import br.all.application.user.repository.UserAccountRepository
import br.all.application.user.update.PatchUserProfileService.RequestModel
import br.all.application.user.update.PatchUserProfileService.ResponseModel
import br.all.domain.shared.user.Email
import br.all.domain.shared.user.Text
import br.all.domain.shared.user.Username

class PatchUserProfileServiceImpl(
    private val repository: UserAccountRepository
) : PatchUserProfileService {
    override fun patchProfile(
        presenter: PatchUserProfilePresenter,
        request: RequestModel
    ) {
        if (repository.loadCredentialsById(request.userId) == null) {
            presenter.prepareFailView(NoSuchElementException("User with id ${request.userId} not found!"))
        }

        if (presenter.isDone()) return

//        val newUsername = Username(request.username)
//        val newEmail = Email(request.email)
//        val affiliation = Text(request.affiliation)
//        val country = Text(request.country)

        // TODO(): add invalid entries to the response model array
        // TODO(): update only the valid entries

        //    data class RequestModel(
        //        val userId: UUID,
        //        val username: String,
        //        val email: String,
        //        val affiliation: String,
        //        val country: String
        //    )
        //
        //    data class ResponseModel(
        //        val userId: UUID,
        //        val username: String,
        //        val email: String,
        //        val affiliation: String,
        //        val country: String,
        //        val invalidEntries: List<String>
        //    )
    }
}