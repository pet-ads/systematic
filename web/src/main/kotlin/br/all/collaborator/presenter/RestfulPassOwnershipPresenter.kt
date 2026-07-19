package br.all.collaborator.presenter

import br.all.application.collaborator.update.PassOwnershipService.ResponseModel
import br.all.application.collaborator.update.PassOwnershipPresenter
import br.all.shared.error.createErrorResponseFrom
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.status

class RestfulPassOwnershipPresenter : PassOwnershipPresenter {
    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ResponseModel) {
        responseEntity = status(HttpStatus.NO_CONTENT).body(response)
    }

    override fun prepareFailView(throwable: Throwable) = run { responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null
}