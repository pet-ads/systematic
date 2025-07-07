package br.all.study.presenter

import br.all.application.study.update.interfaces.RemoveCriteriaPresenter
import br.all.application.study.update.interfaces.RemoveCriteriaService
import br.all.shared.error.createErrorResponseFrom
import br.all.study.requests.RemoveCriteriaRequest
import br.all.utils.LinksFactory
import org.springframework.hateoas.RepresentationModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class RestfulRemoveCriteriaPresenter(
    private val linksFactory: LinksFactory,
): RemoveCriteriaPresenter {
    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: RemoveCriteriaService.ResponseModel) {
        val restfulResponse = ViewModel(
            response.inclusionCriteria,
            response.exclusionCriteria,
        )

        response.inclusionCriteria.forEach {
            restfulResponse.add(linksFactory.removeStudyReviewCriteria
                (
                response.systematicStudyId,
                response.studyId,
                RemoveCriteriaRequest(
                    criteria = listOf(it)
                    )
                )
            )
        }

        response.exclusionCriteria.forEach {
            restfulResponse.add(linksFactory.removeStudyReviewCriteria
                (
                response.systematicStudyId,
                response.studyId,
                RemoveCriteriaRequest(
                    criteria = listOf(it)
                    )
                )
            )
        }

        val protocol = linksFactory.findProtocol(response.systematicStudyId)
        restfulResponse.add(protocol)
        responseEntity = ResponseEntity.status(HttpStatus.OK).body(restfulResponse)
    }

    override fun prepareFailView(throwable: Throwable) = run {responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    private data class ViewModel(
        val inclusionCriteria: List<String>,
        val exclusionCriteria: List<String>,
    ) : RepresentationModel<ViewModel>()
}