package br.all.report.presenter

import br.all.application.report.find.presenter.FindSourcePresenter
import br.all.application.report.find.service.FindSourceService
import br.all.shared.error.createErrorResponseFrom
import br.all.utils.LinksFactory
import org.springframework.hateoas.RepresentationModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.*

class RestfulFindSourcePresenter(
    private val linksFactory: LinksFactory,
): FindSourcePresenter {
    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: FindSourceService.ResponseModel) {
        val restfulResponse = ViewModel(
            userId = response.userId,
            systematicStudyId = response.systematicStudyId,
            source = response.source,
            included = response.included,
            excluded = response.excluded,
            duplicated = response.duplicated,
            totalOfStudies = response.totalOfStudies,
        )

        val selfRef = linksFactory.findSource(response.systematicStudyId, response.source)
        val findSessions = linksFactory.findSessionsBySource(response.systematicStudyId, response.source)

        restfulResponse.add(selfRef, findSessions)

        responseEntity = ResponseEntity.status(HttpStatus.OK).body(restfulResponse)
    }

    override fun prepareFailView(throwable: Throwable) = run {responseEntity = createErrorResponseFrom(throwable)}

    override fun isDone() = responseEntity != null

    data class ViewModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val source: String,
        val included: List<Long>,
        val excluded: List<Long>,
        val duplicated: List<Long>,
        val totalOfStudies: Int
    ): RepresentationModel<ViewModel>()
}