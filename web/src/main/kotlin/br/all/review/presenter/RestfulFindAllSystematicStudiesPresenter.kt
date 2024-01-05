package br.all.review.presenter

import br.all.application.review.find.presenter.FindAllSystematicStudyPresenter
import br.all.application.review.find.services.FindAllSystematicStudyService
import br.all.application.review.repository.SystematicStudyDto
import br.all.review.controller.SystematicStudyController
import br.all.shared.error.createErrorResponseFrom
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.*

class RestfulFindAllSystematicStudiesPresenter: FindAllSystematicStudyPresenter {
    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: FindAllSystematicStudyService.ResponseModel) {
        val restfulResponse = ViewModel(
            response.researcherId,
            response.systematicStudies.size,
            response.systematicStudies,
        )

        val self = linkTo<SystematicStudyController> {
            findAllSystematicStudies(response.researcherId)
        }.withSelfRel()

        restfulResponse.add(self)
        responseEntity = ResponseEntity.status(HttpStatus.OK).body(restfulResponse)
    }

    override fun prepareFailView(throwable: Throwable) = run { responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    private data class ViewModel(
        val researcherId: UUID,
        val size: Int,
        val content: List<SystematicStudyDto>,
    ): RepresentationModel<ViewModel>()
}