package br.all.reviewmode.presenter

import br.all.application.reviewmode.assign.CheckAvailableStudiesToAssignPresenter
import br.all.application.reviewmode.assign.CheckAvailableStudiesToAssignService.ResponseModel
import br.all.shared.error.createErrorResponseFrom
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok

class RestfulCheckAvailableStudiesToAssignPresenter : CheckAvailableStudiesToAssignPresenter {
    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ResponseModel) {
        responseEntity = ok(response)
    }

    override fun prepareFailView(throwable: Throwable) = run {
        responseEntity = createErrorResponseFrom(throwable)
    }

    override fun isDone() = responseEntity != null
}
