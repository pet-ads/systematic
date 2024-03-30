package br.all.study.presenter


import br.all.application.study.update.interfaces.MarkAsDuplicatedPresenter
import br.all.application.study.update.interfaces.MarkAsDuplicatedService
import br.all.application.study.update.interfaces.MarkAsDuplicatedService.*
import br.all.domain.model.study.StudyReview
import br.all.shared.error.createErrorResponseFrom
import br.all.study.controller.StudyReviewController
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.status
import org.springframework.stereotype.Component
import java.util.*

@Component
class RestfulMarkAsDuplicatedPresenter : MarkAsDuplicatedPresenter {

    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ResponseModel) {
        val restfulResponse = ViewModel(
            response.researcherId,
            response.systematicStudyId,
            response.updatedStudyReview,
            response.duplicatedStudyReview,
        )
    }

    private fun prepareHateoas(response: ResponseModel, restfulResponse: ViewModel) {

        val self = linkTo<StudyReviewController> {
            findStudyReview(response.researcherId, response.systematicStudyId, response.duplicatedStudyReview)
        }.withSelfRel()

        restfulResponse.add(self)
        responseEntity = status(HttpStatus.OK).body(restfulResponse)
    }

    override fun prepareFailView(throwable: Throwable) = run { responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    private data class ViewModel(
        val researcherId: UUID,
        val systematicStudyId: UUID,
        val updatedStudyReviewId: Long,
        val duplicatedStudyReviewId: Long,
    ) : RepresentationModel<ViewModel>()
}