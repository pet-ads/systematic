package br.all.application.user.update

import br.all.application.user.repository.UserAccountRepository
import br.all.application.user.update.PatchUserProfileService.RequestModel
import br.all.application.user.update.PatchUserProfileService.ResponseModel
import br.all.application.user.update.PatchUserProfileService.InvalidEntry
import br.all.domain.shared.user.Email
import br.all.domain.shared.user.Name
import br.all.domain.shared.user.Text

class PatchUserProfileServiceImpl(
    private val repository: UserAccountRepository
) : PatchUserProfileService {
    override fun patchProfile(
        presenter: PatchUserProfilePresenter,
        request: RequestModel
    ) {
        val userAccount = repository.loadFullUserAccountById(request.userId)
        if (userAccount == null) {
            presenter.prepareFailView(NoSuchElementException("User with id ${request.userId} doesn't exist!"))
            return
        }

        val invalidEntries: MutableList<InvalidEntry> = mutableListOf()
        var updatedName = userAccount.name
        var updatedEmail = userAccount.email
        var updatedCountry = userAccount.country
        var updatedAffiliation = userAccount.affiliation

        try {
            val newName = Name(request.name)
            updatedName = newName.value
        } catch (e: Exception) {
            invalidEntries.add(InvalidEntry(
                field = "name",
                entry = request.name,
                message = e.message ?: "Invalid name format"
            ))
        }

        try {
            val newEmail = Email(request.email)
            updatedEmail = newEmail.email
        } catch (e: Exception) {
            invalidEntries.add(InvalidEntry(
                field = "email",
                entry = request.email,
                message = e.message ?: "Invalid email format"
            ))
        }

        try {
            val newCountry = Text(request.country)
            updatedCountry = newCountry.value
        } catch (e: Exception) {
            invalidEntries.add(InvalidEntry(
                field = "country",
                entry = request.country,
                message = e.message ?: "Invalid country format"
            ))
        }

        try {
            val newAffiliation = Text(request.affiliation)
            updatedAffiliation = newAffiliation.value
        } catch (e: Exception) {
            invalidEntries.add(InvalidEntry(
                field = "affiliation",
                entry = request.affiliation,
                message = e.message ?: "Invalid affiliation format"
            ))
        }

        val updatedUserAccount = userAccount.copy(
            name = updatedName,
            email = updatedEmail,
            country = updatedCountry,
            affiliation = updatedAffiliation
        )

        val responseModel = ResponseModel(
            userId = request.userId,
            name = updatedName,
            username = userAccount.username,
            email = updatedEmail,
            affiliation = updatedAffiliation,
            country = updatedCountry,
            invalidEntries = invalidEntries
        )

        repository.save(updatedUserAccount)
        presenter.prepareSuccessView(responseModel)
    }
}