package br.all.security.auth

import br.all.application.user.create.PostPasswordRecoveryTokenPresenter
import br.all.application.user.create.PostPasswordRecoveryTokenService
import br.all.shared.error.createErrorResponseFrom
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class RestfulPostPasswordRecoveryTokenPresenter : PostPasswordRecoveryTokenPresenter {

    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: PostPasswordRecoveryTokenService.ResponseModel) {
        responseEntity = ResponseEntity<Void>(HttpStatus.NO_CONTENT)
    }

    override fun prepareFailView(throwable: Throwable) = run { responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null
}