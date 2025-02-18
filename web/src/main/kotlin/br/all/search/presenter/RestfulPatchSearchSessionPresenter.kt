package br.all.search.presenter

import br.all.application.search.update.PatchSearchSessionPresenter
import br.all.application.search.update.PatchSearchSessionService
import br.all.shared.error.createErrorResponseFrom
import br.all.utils.LinksFactory
import org.springframework.hateoas.RepresentationModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.*

class RestfulPatchSearchSessionPresenter (
    private val linksFactory: LinksFactory
) : PatchSearchSessionPresenter {

    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: PatchSearchSessionService.ResponseModel) {
        val restfulResponse = ViewModel(
            response.userId,
            response.systematicStudyId,
            response.sessionId,
            response.invalidEntries
        )

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
        val invalidEntries: List<String>
    ) : RepresentationModel<ViewModel>()
}