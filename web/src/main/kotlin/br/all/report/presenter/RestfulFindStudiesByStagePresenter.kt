package br.all.report.presenter

import br.all.application.report.find.presenter.FindStudiesByStagePresenter
import br.all.application.report.find.service.FindStudiesByStageService
import br.all.shared.error.createErrorResponseFrom
import br.all.utils.LinksFactory
import org.springframework.hateoas.RepresentationModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.*

class RestfulFindStudiesByStagePresenter(
    private val linksFactory: LinksFactory,
): FindStudiesByStagePresenter {
    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: FindStudiesByStageService.ResponseModel) {
        val restfulResponse = ViewModel(
            response.userId,
            response.systematicStudyId,
            response.stage,
            response.included,
            response.excluded,
            response.unclassified,
            response.duplicated
        )

        val selfRef = linksFactory.findStudiesByStage(response.systematicStudyId, response.stage)

        restfulResponse.add(selfRef)
        responseEntity = ResponseEntity.status(HttpStatus.OK).body(restfulResponse)
    }

    override fun prepareFailView(throwable: Throwable) = run {responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    data class ViewModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val stage: String,
        val includedStudies: FindStudiesByStageService.StudyCollection,
        val excludedStudies: FindStudiesByStageService.StudyCollection,
        val unclassifiedStudies: FindStudiesByStageService.StudyCollection,
        val duplicatedStudies: FindStudiesByStageService.StudyCollection
    ): RepresentationModel<ViewModel>()
}
