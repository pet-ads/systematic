package br.all.collaborator.presenter

import br.all.application.collaborator.invitation.RespondInvitationService.ResponseModel
import br.all.application.collaborator.invitation.ResponseInvitationPresenter
import br.all.shared.error.createErrorResponseFrom
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.status
import org.springframework.stereotype.Component

@Component
class RestfulRespondInvitationPresenter: ResponseInvitationPresenter {
    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ResponseModel) {
        responseEntity = status(HttpStatus.NO_CONTENT).body(response)
    }

    override fun prepareFailView(throwable: Throwable) = run { responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null
}