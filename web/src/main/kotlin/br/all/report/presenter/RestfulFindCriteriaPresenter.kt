package br.all.report.presenter

import br.all.application.report.find.presenter.FindCriteriaPresenter
import br.all.application.report.find.service.FindCriteriaService.ResponseModel
import br.all.shared.error.createErrorResponseFrom
import br.all.utils.LinksFactory
import org.springframework.hateoas.RepresentationModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.*

class RestfulFindCriteriaPresenter(
    private val linksFactory: LinksFactory,
): FindCriteriaPresenter {
    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ResponseModel) {
        val restfulResponse = ViewModel(
            userId = response.userId,
            systematicStudyId = response.systematicStudyId,
            type = response.criteria.included.keys.first().type,
            criteria = response.criteria.included.map { (criterionDto, studyReviewIds) ->
                criterionDto.description to studyReviewIds
            }.toMap()
        )


        val type = response.criteria.included.keys.first().type

        val selfRef = linksFactory.findCriteria(response.systematicStudyId, type)
        val findProtocol = linksFactory.findProtocol(response.systematicStudyId)

        restfulResponse.add(selfRef, findProtocol)
        responseEntity = ResponseEntity.status(HttpStatus.OK).body(restfulResponse)
    }

    override fun prepareFailView(throwable: Throwable) = run {responseEntity = createErrorResponseFrom(throwable)}

    override fun isDone() = responseEntity != null

    data class ViewModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val type: String,
        val criteria: Map<String, List<Long>>,
    ): RepresentationModel<ViewModel>()
}