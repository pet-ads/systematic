package br.all.search.presenter

import br.all.application.search.update.UpdateSearchSessionPresenter
import br.all.application.search.update.UpdateSearchSessionService.ResponseModel
import br.all.search.controller.SearchSessionController
import br.all.shared.error.createErrorResponseFrom
import br.all.utils.LinksFactory
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.*

class RestfulUpdateSearchSessionPresenter(
    private val linksFactory: LinksFactory
) : UpdateSearchSessionPresenter {

    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ResponseModel) {
        val restfulResponse = ViewModel(response.userId, response.systematicStudyId, response.sessionId)

        val self = linksFactory.findSession(response.systematicStudyId, response.sessionId)

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