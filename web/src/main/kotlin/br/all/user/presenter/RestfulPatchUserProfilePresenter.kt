package br.all.user.presenter

import br.all.application.user.update.PatchUserProfilePresenter
import br.all.application.user.update.PatchUserProfileService.InvalidEntry
import br.all.application.user.update.PatchUserProfileService.ResponseModel
import br.all.shared.error.createErrorResponseFrom
import org.springframework.hateoas.RepresentationModel
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import java.util.UUID

class RestfulPatchUserProfilePresenter : PatchUserProfilePresenter {

    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ResponseModel) {
        val restfulResponse = ViewModel(
            userId = response.userId,
            name = response.name,
            username = response.username,
            email = response.email,
            affiliation = response.affiliation,
            country = response.country,
            invalidEntries = response.invalidEntries
        )
        responseEntity = ok(restfulResponse)
    }

    override fun prepareFailView(throwable: Throwable) = run { responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    private data class ViewModel(
        val userId: UUID,
        val name: String,
        val username: String,
        val email: String,
        val affiliation: String,
        val country: String,
        val invalidEntries: List<InvalidEntry>
    ) : RepresentationModel<ViewModel>()
}