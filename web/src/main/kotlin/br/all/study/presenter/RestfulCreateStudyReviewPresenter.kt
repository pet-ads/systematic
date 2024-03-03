package br.all.study.presenter

import br.all.application.study.create.CreateStudyReviewPresenter
import br.all.application.study.create.CreateStudyReviewService.ResponseModel
import br.all.application.study.update.interfaces.MarkAsDuplicatedPresenter
import br.all.application.study.update.interfaces.UpdateStudyReviewStatusService
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
import br.all.application.study.update.interfaces.UpdateStudyReviewStatusService.RequestModel as UpdateStatusRequest

@Component
class RestfulCreateStudyReviewPresenter : CreateStudyReviewPresenter {

    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ResponseModel) {
        val restfulResponse = ViewModel(response.researcherId, response.systematicStudyId, response.studyReviewId)

        val self = linkTo<StudyReviewController> {
            findStudyReview(response.researcherId, response.systematicStudyId, response.studyReviewId)
        }.withSelfRel()

        /*linkTo<StudyReviewController> {
            findAllStudyReviews(response.researcherId, response.systematicStudyId);
        }.withRel("_all")*/

        val findAll = linkTo<StudyReviewController> {
            findAllStudyReviews(response.researcherId, response.systematicStudyId)
        }.withRel("findAll")

        val findAllBySource = linkTo<StudyReviewController> {
            findAllStudyReviewsBySource(response.researcherId, response.systematicStudyId, searchSource = ""
            )
        }.withRel("findAllBySource")

        val findOne = linkTo<StudyReviewController> {
            findStudyReview(response.researcherId, response.systematicStudyId, response.studyReviewId)
        }.withRel("findOne")

        val updateSelectionStatus = linkTo<StudyReviewController> {
            updateStudyReviewSelectionStatus(response.researcherId, response.systematicStudyId, response.studyReviewId, request = UpdateStatusRequest(
                response.researcherId, response.systematicStudyId, response.studyReviewId, status = ""
            ))
        }.withRel("updateSelectionStatus")

        val updateExtractionStatus = linkTo<StudyReviewController> {
            updateStudyReviewExtractionStatus(response.researcherId, response.systematicStudyId, response.studyReviewId, request = UpdateStatusRequest(
                response.researcherId, response.systematicStudyId, response.studyReviewId, status = ""
            ))
        }.withRel("updateExtractionStatus")

        val updateReadingPriority = linkTo<StudyReviewController> {
            updateStudyReviewReadingPriority(response.researcherId, response.systematicStudyId, response.studyReviewId, request = UpdateStatusRequest(
                response.researcherId, response.systematicStudyId, response.studyReviewId, status = ""
            ))
        }.withRel("updateReadingPriority")

        /*
        val markAsDuplicated = linkTo<StudyReviewController> {
            markAsDuplicated(response.researcherId, response.systematicStudyId, studyReviewIdToKeep, studyReviewToMarkAsDuplicated)
        }.withRel("markAsDuplicated")*/


        restfulResponse.add(self,findAll,findAllBySource, findOne, updateSelectionStatus, updateExtractionStatus, updateReadingPriority)
        responseEntity = status(HttpStatus.CREATED).body(restfulResponse)
    }

    override fun prepareFailView(throwable: Throwable) = run {responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    private data class ViewModel(
        val researcherId: UUID,
        val systematicStudyId: UUID,
        val studyReviewId: Long
    ) : RepresentationModel<ViewModel>()

}