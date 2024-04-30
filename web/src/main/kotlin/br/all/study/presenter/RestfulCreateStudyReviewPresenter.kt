package br.all.study.presenter

import br.all.application.study.create.CreateStudyReviewPresenter
import br.all.application.study.create.CreateStudyReviewService.ResponseModel
import br.all.shared.error.createErrorResponseFrom
import br.all.study.controller.StudyReviewController
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.status
import org.springframework.stereotype.Component
import java.util.*
import br.all.application.study.update.interfaces.UpdateStudyReviewStatusService.RequestModel as UpdateStatusRequest

@Component
class RestfulCreateStudyReviewPresenter : CreateStudyReviewPresenter {

    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ResponseModel) {
        val restfulResponse = ViewModel(response.researcherId, response.systematicStudyId, response.studyReviewId)

        val selfRef = linkSelfRef(response)
        val allStudyReview = linkFindAllStudyReview(response)
        val updateSelectionStatus = linkUpdateSelectionStatus(response)
        val updateExtractionStatus = linkUpdateExtractionStatus(response)
        val updateReadingPriority = linkUpdateReadingPriority(response)
        val markAsDuplicated = linkMarkAsDuplicated(response)

        restfulResponse.add(selfRef, allStudyReview, updateSelectionStatus, updateExtractionStatus, updateReadingPriority,
                            markAsDuplicated)
        responseEntity = status(HttpStatus.CREATED).body(restfulResponse)
    }

    private fun linkSelfRef(response: ResponseModel) =
        linkTo<StudyReviewController> {
            findStudyReview(response.researcherId, response.systematicStudyId, response.studyReviewId)
        }.withSelfRel()

    private fun linkFindAllStudyReview(response: ResponseModel) =
        linkTo<StudyReviewController> {
            findAllStudyReviews(response.researcherId, response.systematicStudyId)
        }.withRel("allStudyReview")

    private fun linkUpdateSelectionStatus(response: ResponseModel) =
        linkTo<StudyReviewController> {
            updateStudyReviewSelectionStatus(
                response.researcherId,
                response.systematicStudyId,
                response.studyReviewId,
                request = UpdateStatusRequest(
                    response.researcherId, response.systematicStudyId, response.studyReviewId, status = "status"
                )
            )
        }.withRel("updateSelectionStatus")

    private fun linkUpdateExtractionStatus(response: ResponseModel) =
        linkTo<StudyReviewController> {
            updateStudyReviewExtractionStatus(
                response.researcherId,
                response.systematicStudyId,
                response.studyReviewId,
                request = UpdateStatusRequest(
                    response.researcherId, response.systematicStudyId, response.studyReviewId, status = "status"
                )
            )
        }.withRel("updateExtractionStatus")

    private fun linkUpdateReadingPriority(response: ResponseModel) =
        linkTo<StudyReviewController> {
            updateStudyReviewReadingPriority(
                response.researcherId,
                response.systematicStudyId,
                response.studyReviewId,
                request = UpdateStatusRequest(
                    response.researcherId, response.systematicStudyId, response.studyReviewId, status = "status"
                )
            )
        }.withRel("updateReadingPriority")

    private fun linkMarkAsDuplicated(response: ResponseModel) =
        linkTo<StudyReviewController> {
            markAsDuplicated(
                response.researcherId,
                response.systematicStudyId,
                response.studyReviewId,
                response.studyReviewId
            )
        }.withRel("markAsDuplicated")


    override fun prepareFailView(throwable: Throwable) = run { responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    private data class ViewModel(
        val researcherId: UUID,
        val systematicStudyId: UUID,
        val studyReviewId: Long
    ) : RepresentationModel<ViewModel>()
}