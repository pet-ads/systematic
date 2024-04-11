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

        val selfRef = linkSelfRef(response)
        val allSessions = linkFindAllSearchSessions(response)
        val updateSession = linkUpdateSearchSession(response)

        restfulResponse.add(selfRef, allSessions, updateSession)
        responseEntity = ResponseEntity.status(HttpStatus.CREATED).body(restfulResponse)
    }

    private fun linkSelfRef(response: ResponseModel) =
        linkTo<SearchSessionController> {
            findSearchSession(response.researcherId, response.systematicStudyId, response.sessionId)
        }.withSelfRel()

    private fun linkFindAllSearchSessions(response: ResponseModel) =
        linkTo<SearchSessionController> {
            findAllSearchSessions(
                response.researcherId,
                response.systematicStudyId
            )
        }.withRel("allSessions")

    private fun linkUpdateSearchSession(response: ResponseModel) =
        linkTo<SearchSessionController> {
            updateSearchSession(
                response.researcherId,
                response.systematicStudyId,
                response.sessionId,
                SearchSessionController.PutRequest("searchString",
                    "additionalInfo", "source")
            )
        }.withRel("updateSession")

    override fun prepareFailView(throwable: Throwable) = run { responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    private data class ViewModel(
        val researcherId: UUID,
        val systematicStudyId: UUID,
        val sessionId: UUID,
    ) : RepresentationModel<ViewModel>()
}