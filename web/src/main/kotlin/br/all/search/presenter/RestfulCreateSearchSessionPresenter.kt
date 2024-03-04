package br.all.search.presenter

import br.all.application.search.create.CreateSearchSessionPresenter
import br.all.application.search.create.CreateSearchSessionService.ResponseModel
import br.all.search.controller.SearchSessionController
import br.all.shared.error.createErrorResponseFrom
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import java.util.*

@Component
class RestfulCreateSearchSessionPresenter : CreateSearchSessionPresenter {

    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ResponseModel) {
        val restfulResponse = ViewModel(
            response.researcherId,
            response.systematicStudyId,
            response.sessionId,
        )

        val self = linkTo<SearchSessionController> {
            findSearchSession(response.researcherId, response.systematicStudyId, response.sessionId)
        }.withSelfRel()

        restfulResponse.add(self)

        responseEntity = ResponseEntity.status(HttpStatus.CREATED).body(restfulResponse)
    }

    override fun prepareFailView(throwable: Throwable) = run { responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    private data class ViewModel(
        val researcherId: UUID,
        val systematicStudyId: UUID,
        val sessionId: UUID,
    ) : RepresentationModel<ViewModel>()
}