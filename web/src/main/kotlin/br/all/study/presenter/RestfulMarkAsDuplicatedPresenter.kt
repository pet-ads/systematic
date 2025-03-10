package br.all.study.presenter

import br.all.application.study.update.interfaces.MarkAsDuplicatedPresenter
import br.all.application.study.update.interfaces.MarkAsDuplicatedService.ResponseModel
import br.all.shared.error.createErrorResponseFrom
import br.all.study.controller.StudyReviewController
import br.all.utils.LinksFactory
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.status
import org.springframework.stereotype.Component
import java.util.*

@Component
class RestfulMarkAsDuplicatedPresenter(
    private val linksFactory: LinksFactory
) : MarkAsDuplicatedPresenter {

    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ResponseModel) {
        val restfulResponse = ViewModel(
            researcherId = response.userId,
            systematicStudyId = response.systematicStudyId,
            duplicatedStudies = response.duplicatedStudies
        )
        prepareHateoas(response, restfulResponse)
    }

    private fun prepareHateoas(response: ResponseModel, restfulResponse: ViewModel) {
        val destinationStudyId = response.duplicatedStudies.firstOrNull()
            ?: throw IllegalStateException("No duplicated study found in the response")
        val self = linksFactory.findStudy(response.systematicStudyId, destinationStudyId)
        restfulResponse.add(self)
        responseEntity = status(HttpStatus.OK).body(restfulResponse)
    }


    override fun prepareFailView(throwable: Throwable) {
        responseEntity = createErrorResponseFrom(throwable)
    }

    override fun isDone() = responseEntity != null

    private data class ViewModel(
        val researcherId: UUID,
        val systematicStudyId: UUID,
        val duplicatedStudies: List<Long>
    ) : RepresentationModel<ViewModel>()
}
