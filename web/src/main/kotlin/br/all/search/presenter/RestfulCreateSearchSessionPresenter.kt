package br.all.search.presenter

import br.all.application.search.create.CreateSearchSessionPresenter
import br.all.application.search.create.CreateSearchSessionService.ResponseModel
import br.all.domain.model.researcher.ResearcherId
import br.all.domain.model.review.SystematicStudyId
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
            response.sessionId,
            response.systematicStudyId,
            response.researcherId
        )

/*        val self = linkTo<SearchSessionController> {
            findSearchSession(response.sessionId)
        }.withSelfRel()

        restfulResponse.add(self)*/

        responseEntity = ResponseEntity.status(HttpStatus.CREATED).body(restfulResponse)
    }

    override fun prepareFailView(throwable: Throwable) = run { responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    private data class ViewModel(
        val sessionId: String,
        val systematicStudyId: SystematicStudyId,
        val researcherId: ResearcherId
    ) : RepresentationModel<ViewModel>()
}