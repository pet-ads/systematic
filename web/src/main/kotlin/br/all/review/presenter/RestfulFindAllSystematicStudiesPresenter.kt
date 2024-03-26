package br.all.review.presenter

import br.all.application.review.create.CreateSystematicStudyService
import br.all.application.review.find.presenter.FindAllSystematicStudyPresenter
import br.all.application.review.find.services.FindAllSystematicStudiesService
import br.all.application.review.repository.SystematicStudyDto
import br.all.review.controller.SystematicStudyController
import br.all.shared.error.createErrorResponseFrom
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import java.util.*

class RestfulFindAllSystematicStudiesPresenter: FindAllSystematicStudyPresenter {
    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: FindAllSystematicStudiesService.ResponseModel) {
        val restfulResponse = ViewModel(
            response.researcherId,
            response.systematicStudies.size,
            response.systematicStudies,
            response.ownerId,
        )

        val self = with(response) {
            ownerId?.let { linkToFindAllByOwner(researcherId, it) } ?: linkToFindAll(researcherId)
        }

        restfulResponse.add(self)
        responseEntity = ResponseEntity.status(HttpStatus.OK).body(restfulResponse)
    }

    private fun linkToFindAllByOwner(researcherId: UUID, ownerId: UUID) = linkTo<SystematicStudyController> {
        findAllSystematicStudiesByOwner(researcherId, ownerId)
    }.withSelfRel()

    private fun linkToFindAll(researcherId: UUID) = linkTo<SystematicStudyController> {
        findAllSystematicStudies(researcherId)
    }.withSelfRel()

    private fun postSystematicStudy(researcherId: UUID) = linkTo<SystematicStudyController> {
        postSystematicStudy(
            researcherId,
            request = CreateSystematicStudyService.RequestModel(
                "title",
                "description",
                collaborators = setOf(UUID.randomUUID())))
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