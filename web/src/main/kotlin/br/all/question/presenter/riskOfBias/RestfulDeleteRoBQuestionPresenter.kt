package br.all.question.presenter.riskOfBias

import br.all.application.question.delete.DeleteQuestionPresenter
import br.all.application.question.delete.DeleteQuestionService
import br.all.shared.error.createErrorResponseFrom
import br.all.utils.LinksFactory
import org.springframework.hateoas.RepresentationModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.UUID

class RestfulDeleteRoBQuestionPresenter(
    private val linksFactory: LinksFactory,
) : DeleteQuestionPresenter {
    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: DeleteQuestionService.ResponseModel) {
        val restfulResponse = ViewModel(
            response.userId, response.systematicStudyId, response.questionId, response.affectedStudyReviewIds
        )

        responseEntity = ResponseEntity.status(HttpStatus.OK).body(restfulResponse)
    }

    override fun prepareFailView(throwable: Throwable) = run { responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    private data class ViewModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val questionId: UUID,
        val affectedStudyReviewIds: List<Long>
    ): RepresentationModel<ViewModel>()
}