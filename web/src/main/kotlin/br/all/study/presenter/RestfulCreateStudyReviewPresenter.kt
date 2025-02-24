package br.all.study.presenter

import br.all.application.study.create.CreateStudyReviewPresenter
import br.all.application.study.create.CreateStudyReviewService.ResponseModel
import br.all.shared.error.createErrorResponseFrom
import br.all.study.controller.StudyReviewController
import br.all.study.requests.PatchStatusStudyReviewRequest
import br.all.utils.LinksFactory
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.status
import org.springframework.stereotype.Component
import java.util.*

@Component
class RestfulCreateStudyReviewPresenter(
    private val linksFactory: LinksFactory
) : CreateStudyReviewPresenter {

    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ResponseModel) {
        val restfulResponse = ViewModel(response.userId, response.systematicStudyId, response.studyReviewId)


        val selfRef = linksFactory.findStudy(response.systematicStudyId, response.studyReviewId)
        val allStudyReview = linksFactory.findAllStudies(response.systematicStudyId)
        val updateSelectionStatus = linksFactory.updateStudySelectionStatus(
            response.systematicStudyId, response.studyReviewId
        )
        val updateExtractionStatus = linksFactory.updateStudyExtractionStatus(
            response.systematicStudyId, response.studyReviewId
        )
        val updateReadingPriority = linksFactory.updateStudyReadingPriority(
            response.systematicStudyId, response.studyReviewId
        )
        val markAsDuplicated = linksFactory.markStudyAsDuplicated(response.systematicStudyId)

        restfulResponse.add(selfRef, allStudyReview, updateSelectionStatus, updateExtractionStatus, updateReadingPriority,
                            markAsDuplicated)
        responseEntity = status(HttpStatus.CREATED).body(restfulResponse)
    }

    override fun prepareFailView(throwable: Throwable) = run { responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    private data class ViewModel(
        val researcherId: UUID,
        val systematicStudyId: UUID,
        val studyReviewId: Long
    ) : RepresentationModel<ViewModel>()
}