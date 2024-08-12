package br.all.review.presenter

import br.all.application.review.create.CreateSystematicStudyPresenter
import br.all.application.review.create.CreateSystematicStudyService.ResponseModel
import br.all.review.controller.SystematicStudyController
import br.all.review.requests.PutRequest
import br.all.shared.error.createErrorResponseFrom
import br.all.utils.LinksFactory
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.status
import org.springframework.stereotype.Component
import java.util.*

@Component
class RestfulCreateSystematicStudyPresenter(private val linksFactory: LinksFactory): CreateSystematicStudyPresenter {
    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ResponseModel) {
        val viewModel = ViewModel(response.userId, response.systematicStudyId)

        val selfRef = linksFactory.findReview(response.systematicStudyId)
        val allStudies = linksFactory.findAllReviews()
        val allStudiesByOwner = linksFactory.findMyReviews(response.userId)
        val updateStudy = linksFactory.updateReview(response.systematicStudyId)

        viewModel.add(selfRef, allStudies, allStudiesByOwner, updateStudy)
        responseEntity = status(HttpStatus.CREATED).body(viewModel)
    }

    override fun prepareFailView(throwable: Throwable) = run { responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    private data class ViewModel(
        val researcherId: UUID,
        val systematicStudyId: UUID,
    ): RepresentationModel<ViewModel>()
}