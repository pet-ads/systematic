package br.all.study.presenter

import br.all.application.study.create.CreateStudyReviewPresenter
import br.all.application.study.create.CreateStudyReviewService.ResponseModel
import br.all.shared.error.createErrorResponseFrom
import br.all.study.controller.StudyReviewController
import br.all.study.requests.PatchStatusStudyReviewRequest
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.status
import org.springframework.stereotype.Component
import java.util.*

@Component
class RestfulCreateStudyReviewPresenter : CreateStudyReviewPresenter {

    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ResponseModel) {
        val restfulResponse = ViewModel(response.userId, response.systematicStudyId, response.studyReviewId)

        //TODO remove comment and enable links after updating controllers
//        val selfRef = linkSelfRef(response)
//        val allStudyReview = linkFindAllStudyReview(response)
//        val updateSelectionStatus = linkUpdateSelectionStatus(response)
//        val updateExtractionStatus = linkUpdateExtractionStatus(response)
//        val updateReadingPriority = linkUpdateReadingPriority(response)
//        val markAsDuplicated = linkMarkAsDuplicated(response)

//        restfulResponse.add(selfRef, allStudyReview, updateSelectionStatus, updateExtractionStatus, updateReadingPriority,
//                            markAsDuplicated)
        responseEntity = status(HttpStatus.CREATED).body(restfulResponse)
    }

    private fun linkSelfRef(response: ResponseModel) =
        linkTo<StudyReviewController> {
            findStudyReview(response.systematicStudyId, response.studyReviewId)
        }.withSelfRel()

    private fun linkFindAllStudyReview(response: ResponseModel) =
        linkTo<StudyReviewController> {
            findAllStudyReviews(response.systematicStudyId)
        }.withRel("find-all-studies")

    private fun linkUpdateSelectionStatus(response: ResponseModel) =
        linkTo<StudyReviewController> {
            updateStudyReviewSelectionStatus(
                response.systematicStudyId,
                response.studyReviewId,
                patchRequest = PatchStatusStudyReviewRequest(
                    "status"
                )
            )
        }.withRel("update-study-selection-status")

    private fun linkUpdateExtractionStatus(response: ResponseModel) =
        linkTo<StudyReviewController> {
            updateStudyReviewExtractionStatus(
                response.systematicStudyId,
                response.studyReviewId,
                patchRequest = PatchStatusStudyReviewRequest(
                   "status"
                )
            )
        }.withRel("update-study-extraction-status")

    private fun linkUpdateReadingPriority(response: ResponseModel) =
        linkTo<StudyReviewController> {
            updateStudyReviewReadingPriority(
                response.systematicStudyId,
                response.studyReviewId,
                patchRequest = PatchStatusStudyReviewRequest(
                    "status"
                )
            )
        }.withRel("update-study-reading-priority")

    private fun linkMarkAsDuplicated(response: ResponseModel) =
        linkTo<StudyReviewController> {
            markAsDuplicated(
                response.systematicStudyId,
                response.studyReviewId,
                response.studyReviewId
            )
        }.withRel("mark-study-as-duplicated")


    override fun prepareFailView(throwable: Throwable) = run { responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    private data class ViewModel(
        val researcherId: UUID,
        val systematicStudyId: UUID,
        val studyReviewId: Long
    ) : RepresentationModel<ViewModel>()
}