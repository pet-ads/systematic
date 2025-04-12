package br.all.report.presenter

import br.all.application.report.find.presenter.AuthorNetworkPresenter
import br.all.application.report.find.service.AuthorNetworkService
import br.all.application.report.find.service.FindCriteriaService.ResponseModel
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
            response.studyReviewId,
            response.nodes,
            response.edges
        )

        val selfRef = linksFactory.authorNetwork(response.systematicStudyId, response.studyReviewId)

        restfulResponse.add(selfRef)
        responseEntity = ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body(restfulResponse)
    }

    override fun prepareFailView(throwable: Throwable) = run {responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    data class ViewModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val studyReviewId: Long,
        val nodes: List<UUID>,
        val edges: List<Long>,
    ): RepresentationModel<ViewModel>()
}