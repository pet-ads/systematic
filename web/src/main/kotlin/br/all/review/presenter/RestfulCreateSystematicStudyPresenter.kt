package br.all.review.presenter

import br.all.application.review.create.CreateSystematicStudyPresenter
import br.all.application.review.create.CreateSystematicStudyService.ResponseModel
import br.all.review.controller.SystematicStudyController
import br.all.review.controller.SystematicStudyController.PutRequest
import br.all.shared.error.createErrorResponseFrom
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.status
import org.springframework.stereotype.Component
import java.util.*

@Component
class RestfulCreateSystematicStudyPresenter: CreateSystematicStudyPresenter {
    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ResponseModel) {
        val viewModel = ViewModel(response.researcherId, response.systematicStudyId)

        val selfRef = linkTo<SystematicStudyController> {
            findSystematicStudy(response.researcherId, response.systematicStudyId)
        }.withSelfRel()

        val allStudies = linkTo<SystematicStudyController> {
            findAllSystematicStudies(response.researcherId)
        }.withRel("allStudies")

        val allStudiesByOwner = linkTo<SystematicStudyController> {
            findAllSystematicStudiesByOwner(response.researcherId, response.researcherId)
        }.withRel("allStudiesByOwner")

        val updateStudy = linkTo<SystematicStudyController> {
            updateSystematicStudy(
                response.researcherId,
                response.systematicStudyId,
                PutRequest("title", "description"),
            )
        }.withRel("updateStudy")


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