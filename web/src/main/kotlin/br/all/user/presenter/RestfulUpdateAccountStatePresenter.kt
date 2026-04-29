package br.all.user.presenter

import br.all.application.user.update.UpdateAccountStatePresenter
import br.all.application.user.update.UpdateAccountStateService.ResponseModel
import br.all.shared.error.createErrorResponseFrom
import org.springframework.hateoas.RepresentationModel
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok

class RestfulUpdateAccountStatePresenter : UpdateAccountStatePresenter {
    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ResponseModel) {
        val restfulResponse = ViewModel(
            message = response.message
        )
        responseEntity = ok(restfulResponse)
    }

    override fun prepareFailView(throwable: Throwable) = run { responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    private data class ViewModel(
        val message: String,
    ) : RepresentationModel<ViewModel>()
}