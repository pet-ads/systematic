package br.all.review.presenter

import br.all.application.review.find.presenter.FindAllSystematicStudyPresenter
import br.all.application.review.find.services.FindAllSystematicStudiesService.ResponseModel
import br.all.application.review.repository.SystematicStudyDto
import br.all.review.controller.SystematicStudyController
import br.all.review.requests.PostRequest
import br.all.shared.error.createErrorResponseFrom
import br.all.utils.LinksFactory
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.*

class RestfulFindAllSystematicStudiesPresenter(
    private val linksFactory: LinksFactory
): FindAllSystematicStudyPresenter {
    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ResponseModel) {
        val restfulResponse = ViewModel(
            response.userId,
            response.systematicStudies.size,
            response.systematicStudies,
            response.ownerId,
        )

        val self = with(response) {
            ownerId?.let { linksFactory.findMyReviews(it) } ?: linksFactory.findAllReviews()
        }

        restfulResponse.add(self, linksFactory.createReview())
        responseEntity = ResponseEntity.status(HttpStatus.OK).body(restfulResponse)
    }

    override fun prepareFailView(throwable: Throwable) = run { responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    private data class ViewModel(
        val researcherId: UUID,
        val size: Int,
        val content: List<SystematicStudyDto>,
        val ownerId: UUID?,
    ): RepresentationModel<ViewModel>()
}