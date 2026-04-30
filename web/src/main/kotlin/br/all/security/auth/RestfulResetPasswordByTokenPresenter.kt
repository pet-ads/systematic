package br.all.security.auth

import br.all.application.user.update.ResetPasswordByTokenPresenter
import br.all.application.user.update.ResetPasswordByTokenService
import br.all.shared.error.createErrorResponseFrom
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class RestfulResetPasswordByTokenPresenter : ResetPasswordByTokenPresenter {

    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ResetPasswordByTokenService.ResponseModel) {
        responseEntity = ResponseEntity<Void>(HttpStatus.NO_CONTENT)
    }

    override fun prepareFailView(throwable: Throwable) = run { responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null
}