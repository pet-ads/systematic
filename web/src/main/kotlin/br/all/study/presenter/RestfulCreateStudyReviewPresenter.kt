package br.all.study.presenter

import br.all.application.shared.exceptions.EntityNotFoundException
import br.all.application.shared.exceptions.UnauthenticatedUserException
import br.all.application.shared.exceptions.UnauthorizedUserException
import br.all.application.study.create.CreateStudyReviewPresenter
import br.all.application.study.create.CreateStudyReviewService.ResponseModel
import br.all.study.controller.StudyReviewController
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class RestfulCreateStudyReviewPresenter : CreateStudyReviewPresenter {

    final lateinit var responseEntity: ResponseEntity<*>

    override fun prepareSuccessView(response: ResponseModel) {
        val restfulResponse = ViewModel(response.reviewId, response.studyId)

        val self = linkTo<StudyReviewController> {
            findStudyReview(response.reviewerId, response.reviewId, response.studyId)
        }.withSelfRel()

        restfulResponse.add(self)
        responseEntity = ResponseEntity.status(HttpStatus.CREATED).body(restfulResponse)
    }

    override fun prepareFailView(throwable: Throwable) {
        responseEntity = when (throwable){
            is UnauthenticatedUserException -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("")
            is UnauthorizedUserException -> ResponseEntity.status(HttpStatus.FORBIDDEN).body("")
            is EntityNotFoundException -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("")
            else -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
        }
    }

    private data class ViewModel (val reviewId : UUID, val studyId: Long) : RepresentationModel<ViewModel>()


}