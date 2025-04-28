package br.all.study.presenter

import br.all.application.study.update.interfaces.AnswerQuestionPresenter
import br.all.application.study.update.interfaces.AnswerQuestionService
import br.all.shared.error.createErrorResponseFrom
import br.all.utils.LinksFactory
import org.springframework.hateoas.RepresentationModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.*

class RestfulAnswerQuestionPresenter(
    private val linksFactory: LinksFactory,
): AnswerQuestionPresenter {
    var responseEntity: ResponseEntity<*>?= null

    override fun prepareSuccessView(response: AnswerQuestionService.ResponseModel) {
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