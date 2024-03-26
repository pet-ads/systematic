package br.all.study.presenter

import br.all.application.study.create.CreateStudyReviewService
import br.all.application.study.update.interfaces.UpdateStudyReviewStatusPresenter
import br.all.application.study.update.interfaces.UpdateStudyReviewStatusService
import br.all.application.study.update.interfaces.UpdateStudyReviewStatusService.ResponseModel
import br.all.shared.error.createErrorResponseFrom
import br.all.shared.util.generateTimestamp
import br.all.study.controller.StudyReviewController
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component


//aqui poderia colocar o para mudar o extraction status)
//poderia também criar um link para o próximo estudo e outro para o estudo anterior (considerando o
//id)

@Component
class RestfulUpdateStudyReviewStatusPresenter : UpdateStudyReviewStatusPresenter {

    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ResponseModel) {
        val restfulResponse = ViewModel()
    }

    override fun prepareFailView(throwable: Throwable) =
        run { responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    private data class ViewModel(
        val timestamp: String = generateTimestamp()
    ) : RepresentationModel<ViewModel>()


    private fun prepareHateoas(response: ResponseModel, restfulResponse: ViewModel) {
        val self = linkTo<StudyReviewController> {
            findStudyReview(response.researcherId,
                response.systematicStudyId,
                response.studyReviewId)
        }.withSelfRel()

        val updateExtractionStatus = linkTo<StudyReviewController> {
            updateStudyReviewExtractionStatus(
                response.researcherId,
                response.systematicStudyId,
                response.studyReviewId,
                request = UpdateStudyReviewStatusService.RequestModel(
                    response.researcherId, response.systematicStudyId, response.studyReviewId, status = "status")
            )
        }.withRel("updateExtractionStatus")

        restfulResponse.add(self, updateExtractionStatus)
        responseEntity = ResponseEntity.status(HttpStatus.OK).body(restfulResponse)
    }
}