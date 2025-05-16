package br.all.collaboration.presenter

import br.all.application.collaboration.create.CreateInvitePresenter
import br.all.application.collaboration.create.CreateInviteService
import br.all.shared.error.createErrorResponseFrom
import org.springframework.hateoas.RepresentationModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.*

class RestfulCreateInvitePresenter : CreateInvitePresenter {
    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: CreateInviteService.ResponseModel) {
        responseEntity = ResponseEntity.status(HttpStatus.CREATED).body(ViewModel(response.inviteId))
    }

    override fun prepareFailView(throwable: Throwable) {
        responseEntity = createErrorResponseFrom(throwable)
    }

    override fun isDone() = responseEntity != null

    private data class ViewModel(
        val inviteId: UUID,
    ) : RepresentationModel<ViewModel>()
    
}