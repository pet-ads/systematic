package br.all.review.presenter

import br.all.application.review.update.presenter.UpdateSystematicStudyPresenter
import br.all.application.review.update.services.UpdateSystematicStudyService.ResponseModel
import br.all.review.controller.SystematicStudyController
import br.all.review.requests.PostRequest
import br.all.shared.error.createErrorResponseFrom
import br.all.utils.LinksFactory
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.*

class RestfulUpdateSystematicStudyPresenter(
    private val linksFactory: LinksFactory
): UpdateSystematicStudyPresenter {
    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ResponseModel) {
        val restfulResponse = ViewModel(response.userId, response.systematicStudy)

        val self = linksFactory.findReview(response.systematicStudy)

        restfulResponse.add(self,
            linksFactory.createReview())
        responseEntity = ResponseEntity.status(HttpStatus.OK).body(restfulResponse)
    }

    override fun prepareFailView(throwable: Throwable) = run { responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    private data class ViewModel(
        val researcherId: UUID,
        val systematicStudyId: UUID,
    ): RepresentationModel<ViewModel>()
}
