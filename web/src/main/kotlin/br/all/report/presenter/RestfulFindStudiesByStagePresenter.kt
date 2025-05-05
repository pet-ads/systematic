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
            response.includedStudies,
            response.excludedStudies,
            response.unclassifiedStudies,
            response.duplicatedStudies
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
        val includedStudies: FindStudiesByStageService.StudiesIdAmount,
        val excludedStudies: FindStudiesByStageService.StudiesIdAmount,
        val unclassifiedStudies: FindStudiesByStageService.StudiesIdAmount,
        val duplicatedStudies: FindStudiesByStageService.StudiesIdAmount
    ): RepresentationModel<ViewModel>()
}
