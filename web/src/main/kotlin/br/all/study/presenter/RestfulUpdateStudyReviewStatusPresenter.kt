package br.all.study.presenter

import br.all.application.study.update.interfaces.UpdateStudyReviewStatusPresenter
import br.all.application.study.update.interfaces.UpdateStudyReviewStatusService.ResponseModel
import br.all.shared.error.createErrorResponseFrom
import br.all.shared.util.generateTimestamp
import br.all.study.controller.StudyReviewController
import br.all.study.requests.PatchStatusStudyReviewRequest
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

@Component
class RestfulUpdateStudyReviewStatusPresenter : UpdateStudyReviewStatusPresenter {

    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ResponseModel) {
        val restfulResponse = ViewModel()

        val selfRef = linkSelfRef(response)
        val updateExtractionStatus = linkUpdateExtractionStatus(response)

        restfulResponse.add(selfRef, updateExtractionStatus)
        responseEntity = ResponseEntity.status(HttpStatus.OK).body(restfulResponse)
    }

    private fun linkSelfRef(response: ResponseModel) =
        linkTo<StudyReviewController> {
            findStudyReview(
                response.systematicStudyId,
                response.studyReviewId)
        }.withSelfRel()

    private fun linkUpdateExtractionStatus(response: ResponseModel) =
        linkTo<StudyReviewController> {
            updateStudyReviewExtractionStatus(
                response.systematicStudyId,
                response.studyReviewId,
                patchRequest = PatchStatusStudyReviewRequest(
                    status = "status"
                )
            )
        }.withRel("update-study-extraction-status")


    override fun prepareFailView(throwable: Throwable) =
        run { responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    private data class ViewModel(
        val timestamp: String = generateTimestamp()
    ) : RepresentationModel<ViewModel>()

}