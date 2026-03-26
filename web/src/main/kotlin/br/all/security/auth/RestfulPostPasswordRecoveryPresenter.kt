package br.all.security.auth

import br.all.application.user.update.PostPasswordRecoveryPresenter
import br.all.application.user.update.PostPasswordRecoveryService
import br.all.shared.error.createErrorResponseFrom
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class RestfulPostPasswordRecoveryPresenter : PostPasswordRecoveryPresenter {

    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: PostPasswordRecoveryService.ResponseModel) {
        responseEntity = ResponseEntity<Void>(HttpStatus.NO_CONTENT)
    }

    override fun prepareFailView(throwable: Throwable) = run { responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null
}