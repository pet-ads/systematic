package br.all.report.presenter

import br.all.application.report.find.presenter.FindKeywordsPresenter
import br.all.application.report.find.service.FindKeywordsService
import br.all.shared.error.createErrorResponseFrom
import br.all.utils.LinksFactory
import org.springframework.hateoas.RepresentationModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.*

class RestfulFindKeywordsPresenter(
    private val linksFactory: LinksFactory
): FindKeywordsPresenter {
    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: FindKeywordsService.ResponseModel) {
        val restfulResponse = ViewModel(
            response.userId,
            response.systematicStudyId,
            response.filter,
            response.keywords,
            response.keywordsQuantity,
        )

        val selfRef = linksFactory.keywords(response.systematicStudyId, response.filter)

        restfulResponse.add(selfRef)

        responseEntity = ResponseEntity.status(HttpStatus.OK).body(restfulResponse)
    }

    override fun prepareFailView(throwable: Throwable) = run {responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    data class ViewModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val filter: String?,
        val keywords: List<String>,
        val totalOfKeywords: Int,
    ): RepresentationModel<ViewModel>()
}