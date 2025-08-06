package br.all.user.presenter

import br.all.application.user.find.RetrieveUserProfileService.ResponseModel
import br.all.application.user.find.RetrieveUserProfilePresenter
import br.all.shared.error.createErrorResponseFrom
import org.springframework.hateoas.RepresentationModel
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import java.util.*

class RestfulRetrieveUserProfilePresenter : RetrieveUserProfilePresenter {

    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ResponseModel) {
        val restfulResponse = ViewModel(
            response.userId,
            response.username,
            response.email,
            response.affiliation,
            response.country,
            response.authorities
        )
        responseEntity = ok(restfulResponse)
    }

    override fun prepareFailView(throwable: Throwable) = run { responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    private data class ViewModel(
        val userId: UUID,
        val username: String,
        val email: String,
        val affiliation: String,
        val country: String,
        val authorities: Set<String>
    ) : RepresentationModel<ViewModel>()
}