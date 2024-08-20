package br.all.question.presenter.extraction

import br.all.application.question.update.presenter.UpdateQuestionPresenter
import br.all.application.question.update.services.UpdateQuestionService.ResponseModel
import br.all.shared.error.createErrorResponseFrom
import br.all.utils.LinksFactory
import org.springframework.hateoas.RepresentationModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.*

class RestfulUpdateExtractionQuestionPresenter(
    private val linksFactory: LinksFactory
): UpdateQuestionPresenter {
    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ResponseModel) {
        val restfulResponse = RestfulUpdateExtractionQuestionPresenter.ViewModel(
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