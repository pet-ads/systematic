package br.all.review.presenter

import br.all.application.review.find.presenter.FindAllSystematicStudyPresenter
import br.all.application.review.find.services.FindAllSystematicStudiesService.ResponseModel
import br.all.application.review.repository.SystematicStudyDto
import br.all.review.controller.SystematicStudyController
import br.all.review.requests.PostRequest
import br.all.shared.error.createErrorResponseFrom
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.*

class RestfulFindAllSystematicStudiesPresenter: FindAllSystematicStudyPresenter {
    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ResponseModel) {
        val restfulResponse = ViewModel(
            response.userId,
            response.systematicStudies.size,
            response.systematicStudies,
            response.ownerId,
        )

        val self = with(response) {
            ownerId?.let { linkToFindAllByOwner(it) } ?: linkToFindAll()
        }

        restfulResponse.add(self, postSystematicStudy())
        responseEntity = ResponseEntity.status(HttpStatus.OK).body(restfulResponse)
    }

    private fun linkToFindAllByOwner(ownerId: UUID) = linkTo<SystematicStudyController> {
        findAllSystematicStudiesByOwner(ownerId)
    }.withSelfRel()

    private fun linkToFindAll() = linkTo<SystematicStudyController> {
        findAllSystematicStudies()
    }.withSelfRel()

    private fun postSystematicStudy() = linkTo<SystematicStudyController> {
        postSystematicStudy(PostRequest("title", "description", setOf(UUID.randomUUID())))
    }.withSelfRel()

    override fun prepareFailView(throwable: Throwable) = run { responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    private data class ViewModel(
        val researcherId: UUID,
        val size: Int,
        val content: List<SystematicStudyDto>,
        val ownerId: UUID?,
    ): RepresentationModel<ViewModel>()
}