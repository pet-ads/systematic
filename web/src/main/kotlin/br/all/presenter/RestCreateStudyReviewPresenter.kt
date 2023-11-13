package br.all.presenter

import br.all.application.study.create.CreateStudyReviewPresenter
import br.all.application.study.create.CreateStudyReviewService.ResponseModel
import br.all.controller.StudyReviewController
import br.all.domain.model.study.StudyReview
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class RestCreateStudyReviewPresenter : CreateStudyReviewPresenter {

    final lateinit var responseEntity: ResponseEntity<ViewModel>

    override fun prepareSuccessView(response: ResponseModel) {
        val restfulResponse = ViewModel(response.reviewId, response.studyId)
        val self = linkTo<StudyReviewController> { findStudyReview(response.reviewId, response.studyId) }.withSelfRel()
        restfulResponse.add(self)
        responseEntity = ResponseEntity.ok(restfulResponse)
    }

    override fun prepareFailView(throwable: Throwable) {
        throw throwable
    }

    data class ViewModel (val reviewId : UUID, val studyId: Long) : RepresentationModel<ViewModel>()
}