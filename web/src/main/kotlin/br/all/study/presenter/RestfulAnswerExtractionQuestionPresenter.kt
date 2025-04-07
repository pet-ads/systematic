package br.all.study.presenter

import br.all.application.study.update.interfaces.AnswerExtractionQuestionPresenter
import br.all.application.study.update.interfaces.AnswerExtractionQuestionService
import br.all.shared.error.createErrorResponseFrom
import br.all.utils.LinksFactory
import org.springframework.hateoas.RepresentationModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.*

class RestfulAnswerExtractionQuestionPresenter (
    private val linksFactory: LinksFactory
): AnswerExtractionQuestionPresenter {
    var responseEntity: ResponseEntity<*>?= null

    override fun prepareSuccessView(response: AnswerExtractionQuestionService.ResponseModel) {
        val viewModel = ViewModel(response.userId, response.systematicStudyId, response.studyReviewId)

        val link = linksFactory.findStudy(response.systematicStudyId, response.studyReviewId)
        viewModel.add(link)

        responseEntity = ResponseEntity.status(HttpStatus.OK).body(viewModel)
    }

    override fun prepareFailView(throwable: Throwable) = run { responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    data class ViewModel(
        val researcherId: UUID,
        val systematicStudyId: UUID,
        val studyReviewId: Long,
    ): RepresentationModel<ViewModel>()
}