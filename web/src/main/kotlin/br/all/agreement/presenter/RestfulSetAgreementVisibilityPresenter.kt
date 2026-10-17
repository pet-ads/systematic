package br.all.agreement.presenter

import br.all.application.agreement.visibility.SetAgreementVisibilityPresenter
import br.all.application.agreement.visibility.SetAgreementVisibilityService.ResponseModel
import br.all.shared.error.createErrorResponseFrom
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok

class RestfulSetAgreementVisibilityPresenter : SetAgreementVisibilityPresenter {
    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ResponseModel) {
        responseEntity = ok(response)
    }

    override fun prepareFailView(throwable: Throwable) = run {
        responseEntity = createErrorResponseFrom(throwable)
    }

    override fun isDone() = responseEntity != null
}