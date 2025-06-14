package br.all.report.presenter

import br.all.application.report.find.presenter.FindStudyReviewCriteriaPresenter
import br.all.application.report.find.service.FindStudyReviewCriteriaService
import br.all.shared.error.createErrorResponseFrom
import br.all.utils.LinksFactory
import org.springframework.hateoas.RepresentationModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.status

class RestfulFindStudyReviewCriteriaPresenter(
    private val linksFactory: LinksFactory
): FindStudyReviewCriteriaPresenter {
    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: FindStudyReviewCriteriaService.ResponseModel) {
        val restfulResponse = ViewModel(
            response.exclusionCriteria,
            response.inclusionCriteria
        )

        val selfRef = linksFactory.findStudyReviewCriteria(response.systematicStudyId, response.studyReviewId)

        restfulResponse.add(selfRef)
        responseEntity = status(HttpStatus.OK).body(restfulResponse)
    }

    override fun prepareFailView(throwable: Throwable)= run { responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    private data class ViewModel(
        val exclusionCriteria: Set<String>,
        val inclusionCriteria: Set<String>,
    ) : RepresentationModel<ViewModel>()
}