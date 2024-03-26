package br.all.review.presenter

import br.all.application.review.create.CreateSystematicStudyService
import br.all.application.review.find.presenter.FindOneSystematicStudyPresenter
import br.all.application.review.find.services.FindOneSystematicStudyService.ResponseModel
import br.all.application.review.repository.SystematicStudyDto
import br.all.application.review.update.services.UpdateSystematicStudyService
import br.all.application.study.create.CreateStudyReviewService
import br.all.review.controller.SystematicStudyController
import br.all.shared.error.createErrorResponseFrom
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.PathVariable
import java.util.*

@Component
class RestfulFindOneSystematicStudyPresenter: FindOneSystematicStudyPresenter {
    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ResponseModel) {
        val restfulResponse = ViewModel(response.content)

        val self = linkTo<SystematicStudyController> {
            findSystematicStudy(response.researcherId, response.systematicStudyId)
        }.withSelfRel()

        restfulResponse.add(self)
        responseEntity = ResponseEntity.status(HttpStatus.OK).body(restfulResponse)
    }

    private fun postSystematicStudy(researcherId: UUID) = linkTo<SystematicStudyController> {
        postSystematicStudy(
            researcherId,
            request = CreateSystematicStudyService.RequestModel(
                "title",
                "description",
                collaborators = setOf(UUID.randomUUID())))
    }.withSelfRel()

    private fun updateSystematicStudy(researcherId: UUID, systematicStudyId: UUID) = linkTo<SystematicStudyController> {
        updateSystematicStudy(
            researcherId,
            systematicStudyId,
            request = UpdateSystematicStudyService.RequestModel(
                "title",
                "description"))
    }.withSelfRel()

    override fun prepareFailView(throwable: Throwable)= run { responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    private data class ViewModel(val content: SystematicStudyDto) : RepresentationModel<ViewModel>()
}