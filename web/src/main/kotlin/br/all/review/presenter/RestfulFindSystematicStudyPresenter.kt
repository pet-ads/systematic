package br.all.review.presenter

import br.all.application.review.find.presenter.FindSystematicStudyPresenter
import br.all.application.review.find.services.FindSystematicStudyService.ResponseModel
import br.all.application.review.repository.SystematicStudyDto
import br.all.review.controller.SystematicStudyController
import br.all.review.requests.PostRequest
import br.all.review.controller.SystematicStudyController.PutRequest
import br.all.shared.error.createErrorResponseFrom
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import java.util.*

@Component
class RestfulFindSystematicStudyPresenter : FindSystematicStudyPresenter {
    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ResponseModel) {
        val restfulResponse = ViewModel(response.content)

        val self = linkTo<SystematicStudyController> {
            findSystematicStudy(response.systematicStudyId)
        }.withSelfRel()

        restfulResponse.add(
            self, postSystematicStudy(response.researcherId), updateSystematicStudy(response.systematicStudyId)
        )
        responseEntity = ResponseEntity.status(HttpStatus.OK).body(restfulResponse)
    }

    private fun postSystematicStudy(researcherId: UUID) = linkTo<SystematicStudyController> {
        postSystematicStudy(PostRequest("title", "description", setOf(UUID.randomUUID())))
    }.withSelfRel()

    private fun updateSystematicStudy(systematicStudyId: UUID) = linkTo<SystematicStudyController> {
        updateSystematicStudy(systematicStudyId, PutRequest("title", "description"))
    }.withSelfRel()

    override fun prepareFailView(throwable: Throwable) = run { responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    private data class ViewModel(val content: SystematicStudyDto) : RepresentationModel<ViewModel>()
}