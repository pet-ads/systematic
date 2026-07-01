package br.all.review.presenter

import br.all.application.review.create.InviteCollaboratorService.ResponseModel
import br.all.application.review.create.InviteCollaboratorPresenter
import br.all.application.user.repository.TokenStatus
import br.all.shared.error.createErrorResponseFrom
import org.springframework.hateoas.RepresentationModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.status
import org.springframework.stereotype.Component

@Component
class RestfulCreateInviteCollaboratorPresenter: InviteCollaboratorPresenter {
    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ResponseModel) {
        val viewModel = ViewModel(response.collaboratorUsername, response.collaboratorEmail, response.inviteStatus)
        responseEntity = status(HttpStatus.CREATED).body(viewModel)
    }

    override fun prepareFailView(throwable: Throwable) = run { responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    private data class ViewModel(
        val collaboratorUsername: String,
        val collaboratorEmail: String,
        val inviteStatus: TokenStatus,
    ): RepresentationModel<ViewModel>()
}