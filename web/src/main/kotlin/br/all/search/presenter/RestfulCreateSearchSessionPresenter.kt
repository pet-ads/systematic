package br.all.search.presenter

import br.all.application.search.create.CreateSearchSessionPresenter
import br.all.application.search.create.CreateSearchSessionService.ResponseModel
import br.all.search.controller.SearchSessionController
import br.all.shared.error.createErrorResponseFrom
import br.all.utils.LinksFactory
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import java.util.*

@Component
class RestfulCreateSearchSessionPresenter(
    private val linksFactory: LinksFactory
) : CreateSearchSessionPresenter {
    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ResponseModel) {
        val restfulResponse = ViewModel(
            response.userId,
            response.systematicStudyId,
            response.sessionId,
            response.invalidEntries
        )

        val selfRef = linksFactory.findSession(response.systematicStudyId, response.sessionId)
        val allSessions = linksFactory.findAllSessions(response.systematicStudyId)
        val updateSession = linksFactory.updateSession(response.systematicStudyId, response.sessionId)

        restfulResponse.add(selfRef, allSessions, updateSession)
        responseEntity = ResponseEntity.status(HttpStatus.CREATED).body(restfulResponse)
    }

    override fun prepareFailView(throwable: Throwable) = run { responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    private data class ViewModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val sessionId: UUID,
        val invalidEntries: List<String>
    ) : RepresentationModel<ViewModel>()
}