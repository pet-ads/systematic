package br.all.study.presenter

import br.all.application.study.create.CreateStudyReviewPresenter
import br.all.application.study.create.CreateStudyReviewService.ResponseModel
import br.all.application.study.update.interfaces.UpdateStudyReviewPresenter
import br.all.application.study.update.interfaces.UpdateStudyReviewService
import br.all.shared.error.createErrorResponseFrom
import br.all.study.controller.StudyReviewController
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.hateoas.server.mvc.withRel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.status
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class RestfulUpdateStudyReviewPresenter : UpdateStudyReviewPresenter {

    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: UpdateStudyReviewService.ResponseModel) {
        val restfulResponse = ViewModel(response.researcherId, response.systematicStudyId, response.studyReviewId)

        val self = linkTo<StudyReviewController> {
            findStudyReview(response.researcherId, response.systematicStudyId, response.studyReviewId)
        }.withSelfRel()

        linkTo<StudyReviewController> {
            findAllStudyReviews(response.researcherId, response.systematicStudyId);
        }.withRel("_all")

        // TODO add link to update study review
        // TODO add link to update study review

        restfulResponse.add(self)
        responseEntity = status(HttpStatus.OK).body(restfulResponse)
    }

    override fun prepareFailView(throwable: Throwable) = run {responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    private data class ViewModel(
        val researcherId: UUID,
        val systematicStudyId: UUID,
        val studyReviewId: Long
    ) : RepresentationModel<ViewModel>()

}