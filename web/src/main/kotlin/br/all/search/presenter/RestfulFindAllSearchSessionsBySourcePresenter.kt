package br.all.search.presenter

import br.all.application.search.find.presenter.FindAllSearchSessionsBySourcePresenter
import br.all.application.search.find.service.FindAllSearchSessionsBySourceService.ResponseModel
import br.all.application.search.find.service.FindAllSearchSessionsService
import br.all.application.search.repository.SearchSessionDto
import br.all.search.controller.SearchSessionController
import br.all.shared.error.createErrorResponseFrom
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import java.util.*

@Component
class RestfulFindAllSearchSessionsBySourcePresenter: FindAllSearchSessionsBySourcePresenter {

    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ResponseModel) {
        val restfulResponse = ViewModel(
            response.systematicStudyId,
            response.source,
            response.searchSessions.size,
            response.searchSessions
        )

        val self = linkTo<SearchSessionController> {
            findSearchSessionsBySource(response.systematicStudyId, response.source)
        }.withSelfRel()

        restfulResponse.add(self)
        responseEntity = ResponseEntity.status(HttpStatus.OK).body(restfulResponse)

    }

    override fun prepareFailView(throwable: Throwable) = run { responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null
    private data class ViewModel (
        val systematicStudyId: UUID,
        val source: String,
        val size: Int,
        val searchSessions: List<SearchSessionDto>
    ) : RepresentationModel<ViewModel>()
}