package br.all.user.presenter

import br.all.application.user.update.ChangeAccountPasswordPresenter
import br.all.application.user.update.ChangeAccountPasswordService.ResponseModel
import br.all.shared.error.createErrorResponseFrom
import org.springframework.hateoas.RepresentationModel
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import java.util.UUID

class RestfulChangeAccountPasswordPresenter : ChangeAccountPasswordPresenter {

    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ResponseModel) {
        val restfulResponse = ViewModel(
            userId = response.userId
        )
        responseEntity = ok(restfulResponse)
    }

    override fun prepareFailView(throwable: Throwable) = run { responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    private data class ViewModel(
        val userId: UUID
    ) : RepresentationModel<ViewModel>()
}