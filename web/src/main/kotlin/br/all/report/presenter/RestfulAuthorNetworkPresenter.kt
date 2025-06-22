package br.all.report.presenter

import br.all.application.report.find.presenter.AuthorNetworkPresenter
import br.all.application.report.find.service.AuthorNetworkService
import br.all.application.report.find.service.AuthorNetworkService.Edge
import br.all.application.report.find.service.AuthorNetworkService.PaperNode
import br.all.shared.error.createErrorResponseFrom
import br.all.utils.LinksFactory
import org.springframework.hateoas.RepresentationModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.*

class RestfulAuthorNetworkPresenter(
    private val linksFactory: LinksFactory
): AuthorNetworkPresenter {
    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: AuthorNetworkService.ResponseModel) {
        val restfulResponse = ViewModel(
            response.userId,
            response.systematicStudyId,
            response.nodes,
            response.edges
        )

        val selfRef = linksFactory.authorNetwork(response.systematicStudyId)

        restfulResponse.add(selfRef)
        responseEntity = ResponseEntity.status(HttpStatus.OK).body(restfulResponse)
    }

    override fun prepareFailView(throwable: Throwable) = run {responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    data class ViewModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val nodes: Set<PaperNode>,
        val edges: List<Edge>,
    ): RepresentationModel<ViewModel>()
}