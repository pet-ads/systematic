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

        val selfRef = linkSelfRef(response)
        val allStudies = linkForAllStudies(response)
        val allStudiesByOwner = linkForAllStudiesByOwner(response)
        val updateStudy = linkForUpdatingTheStudy(response)

        viewModel.add(selfRef, allStudies, allStudiesByOwner, updateStudy)
        responseEntity = status(HttpStatus.CREATED).body(viewModel)
    }

    private fun linkForUpdatingTheStudy(response: ResponseModel) =
        linkTo<SystematicStudyController> {
            updateSystematicStudy(
                response.researcherId,
                response.systematicStudyId,
                PutRequest("title", "description"),
            )
        }.withRel("updateStudy")

    private fun linkForAllStudiesByOwner(response: ResponseModel) =
        linkTo<SystematicStudyController> {
            findAllSystematicStudiesByOwner(response.researcherId, response.researcherId)
        }.withRel("allStudiesByOwner")

    private fun linkForAllStudies(response: ResponseModel) =
        linkTo<SystematicStudyController> {
            findAllSystematicStudies(response.researcherId)
        }.withRel("allStudies")

    private fun linkSelfRef(response: ResponseModel) =
        linkTo<SystematicStudyController> {
            findSystematicStudy(response.researcherId, response.systematicStudyId)
        }.withSelfRel()

    override fun prepareFailView(throwable: Throwable) = run { responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    private data class ViewModel(
        val researcherId: UUID,
        val systematicStudyId: UUID,
    ): RepresentationModel<ViewModel>()
}