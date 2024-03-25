package br.all.search.presenter

import br.all.application.search.update.UpdateSearchSessionPresenter
import br.all.application.search.update.UpdateSearchSessionService.ResponseModel
import br.all.search.controller.SearchSessionController
import br.all.shared.error.createErrorResponseFrom
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.*

class RestfulUpdateSearchSessionPresenter : UpdateSearchSessionPresenter {

    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ResponseModel) {
        val restfulResponse = ViewModel(response.researcherId, response.systematicStudyId, response.sessionId)

        val self = linkTo<SearchSessionController> {
            findSearchSession(response.researcherId, response.systematicStudyId, response.sessionId)
        }.withSelfRel()

        restfulResponse.add(self)
        responseEntity = ResponseEntity.status(HttpStatus.OK).body(restfulResponse)
    }

    override fun prepareFailView(throwable: Throwable) = run {responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    private data class ViewModel(
        val researcherId: UUID,
        val systematicStudyId: UUID,
        val searchSessionID: UUID,
    ) : RepresentationModel<ViewModel>()
}