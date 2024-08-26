package br.all.question.presenter.riskOfBias

import br.all.application.question.update.presenter.UpdateQuestionPresenter
import br.all.application.question.update.services.UpdateQuestionService
import br.all.shared.error.createErrorResponseFrom
import br.all.utils.LinksFactory
import org.springframework.hateoas.RepresentationModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.*

class RestfulUpdateRoBQuestionPresenter (
    private val linksFactory: LinksFactory
): UpdateQuestionPresenter {
    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: UpdateQuestionService.ResponseModel) {
        val restfulResponse = RestfulUpdateRoBQuestionPresenter.ViewModel(
            response.userId, response.systematicStudyId, response.questionId
        )

        responseEntity = ResponseEntity.status(HttpStatus.OK).body(restfulResponse)
    }

    override fun prepareFailView(throwable: Throwable) = run { responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    private data class ViewModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val questionId: UUID
    ): RepresentationModel<ViewModel>()
}