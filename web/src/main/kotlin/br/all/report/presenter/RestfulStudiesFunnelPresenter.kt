package br.all.report.presenter

import br.all.application.report.find.presenter.StudiesFunnelPresenter
import br.all.application.report.find.service.StudiesFunnelService
import br.all.shared.error.createErrorResponseFrom
import br.all.utils.LinksFactory
import org.springframework.hateoas.RepresentationModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.*

class RestfulStudiesFunnelPresenter(
    private val linksFactory: LinksFactory,
): StudiesFunnelPresenter {
    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: StudiesFunnelService.ResponseModel) {
        val restfulResponse = ViewModel(
            response.userId,
            response.systematicStudyId,
            response.totalStudies,
            response.totalAfterDuplicates,
            response.totalOfExcludedStudies,
            response.totalExcludedInExtraction,
            response.totalIncluded
        )

        val selfRef = linksFactory.studiesFunnel(response.systematicStudyId)

        restfulResponse.add(selfRef)
        responseEntity = ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body(restfulResponse)
    }

    override fun prepareFailView(throwable: Throwable) = run {responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    data class ViewModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val totalStudies: Int,
        val totalAfterDuplicates: Int,
        val totalOfExcludedStudies: Int,
        val totalExcludedInExtraction: Int,
        val totalIncluded: Int
    ): RepresentationModel<ViewModel>()
}