package br.all.reviewmode.presenter

import br.all.application.reviewmode.assign.AssignStudiesPresenter
import br.all.application.reviewmode.assign.AssignStudiesService.ResponseModel
import br.all.shared.error.createErrorResponseFrom
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok

class RestfulAssignStudiesPresenter : AssignStudiesPresenter {
    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ResponseModel) {
        responseEntity = ok(response)
    }

    override fun prepareFailView(throwable: Throwable) = run {
        responseEntity = createErrorResponseFrom(throwable)
    }

    override fun isDone() = responseEntity != null
}
