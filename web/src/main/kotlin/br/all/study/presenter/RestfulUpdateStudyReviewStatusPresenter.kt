package br.all.study.presenter

import br.all.application.study.update.interfaces.UpdateStudyReviewStatusPresenter
import br.all.application.study.update.interfaces.UpdateStudyReviewStatusService.ResponseModel
import br.all.shared.error.createErrorResponseFrom
import br.all.shared.util.generateTimestamp
import br.all.study.controller.StudyReviewController
import br.all.study.requests.PatchStatusStudyReviewRequest
import br.all.utils.LinksFactory
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

@Component
class RestfulUpdateStudyReviewStatusPresenter(
    private val linksFactory: LinksFactory
) : UpdateStudyReviewStatusPresenter {

    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ResponseModel) {
        val restfulResponse = ViewModel()

        val selfRef = linksFactory.findStudy(response.systematicStudyId, response.studyReviewId)
        val updateExtractionStatus = linksFactory.updateStudyExtractionStatus(
            response.systematicStudyId, response.studyReviewId
        )

        restfulResponse.add(selfRef, updateExtractionStatus)
        responseEntity = ResponseEntity.status(HttpStatus.OK).body(restfulResponse)
    }

    override fun prepareFailView(throwable: Throwable) =
        run { responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    private data class ViewModel(
        val timestamp: String = generateTimestamp()
    ) : RepresentationModel<ViewModel>()

}