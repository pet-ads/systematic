package br.all.search.presenter

import br.all.application.search.find.presenter.FindAllSearchSessionsBySourcePresenter
import br.all.application.search.find.service.FindAllSearchSessionsBySourceService.ResponseModel
import br.all.application.search.repository.SearchSessionDto
import br.all.shared.error.createErrorResponseFrom
import br.all.utils.LinksFactory
import org.springframework.hateoas.RepresentationModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import java.util.*

@Component
class RestfulFindAllSearchSessionsBySourcePresenter(
    private val linksFactory: LinksFactory
): FindAllSearchSessionsBySourcePresenter {

    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ResponseModel) {
        val restfulResponse = ViewModel(
            response.systematicStudyId,
            response.source,
            response.searchSessions.size,
            response.searchSessions
        )

        val self = linksFactory.findSessionsBySource(response.systematicStudyId, response.source)
        val allStudiesBySource = linksFactory.findAllStudiesBySource(response.systematicStudyId, response.source)

        restfulResponse.add(self, allStudiesBySource)

        response.searchSessions.forEach{searchSessionDto ->
            restfulResponse.add(linksFactory.findAllStudiesBySession(response.systematicStudyId, searchSessionDto.id))
        }

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