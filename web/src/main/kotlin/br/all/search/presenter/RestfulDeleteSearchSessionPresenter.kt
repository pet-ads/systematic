package br.all.search.presenter

import br.all.application.search.delete.DeleteSearchSessionPresenter
import br.all.application.search.delete.DeleteSearchSessionService
import br.all.shared.error.createErrorResponseFrom
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class RestfulDeleteSearchSessionPresenter : DeleteSearchSessionPresenter {

    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: DeleteSearchSessionService.ResponseModel) {
        responseEntity = ResponseEntity<Void>(HttpStatus.NO_CONTENT)
    }

    override fun prepareFailView(throwable: Throwable) {
        responseEntity = createErrorResponseFrom(throwable)
    }

    override fun isDone(): Boolean {
        return responseEntity != null
    }
}