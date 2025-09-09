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

        val invalidEntries = mutableListOf<InvalidEntry>()

        val updatedName = validateField("name", request.name, userAccount.name, invalidEntries) { Name(it).value }
        val updatedEmail = validateField("email", request.email, userAccount.email, invalidEntries) { Email(it).email }
        val updatedCountry = validateField("country", request.country, userAccount.country, invalidEntries) { Text(it).value }
        val updatedAffiliation = validateField("affiliation", request.affiliation, userAccount.affiliation, invalidEntries) { Text(it).value }

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

    private fun validateField(
        fieldName: String,
        newValue: String,
        currentValue: String,
        errors: MutableList<InvalidEntry>,
        validationLogic: (String) -> String
    ): String {
        return try {
            validationLogic(newValue)
        } catch (e: Exception) {
            errors.add(InvalidEntry(
                field = fieldName,
                entry = newValue,
                message = e.message ?: "Invalid $fieldName format"
            ))

            currentValue
        }
    }
}