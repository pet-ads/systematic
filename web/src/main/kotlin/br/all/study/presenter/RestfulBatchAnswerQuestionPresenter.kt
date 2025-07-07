package br.all.study.presenter

import br.all.application.study.update.interfaces.BatchAnswerQuestionPresenter
import br.all.application.study.update.interfaces.BatchAnswerQuestionService.ResponseModel
import br.all.application.study.update.interfaces.BatchAnswerQuestionService.FailedAnswer
import br.all.shared.error.createErrorResponseFrom
import br.all.utils.LinksFactory
import org.springframework.hateoas.RepresentationModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.UUID

class RestfulBatchAnswerQuestionPresenter(
    private val linksFactory: LinksFactory
): BatchAnswerQuestionPresenter {
    var responseEntity: ResponseEntity<*>?= null

    override fun prepareSuccessView(response: ResponseModel) {
        val viewModel = ViewModel(
            systematicStudyId = response.systematicStudyId,
            studyReviewId = response.studyReviewId,
            succeededAnswers = response.succeededAnswers,
            failedAnswers = response.failedAnswers,
            totalAnswered = response.totalAnswered
        )

        val link = linksFactory.findStudy(response.systematicStudyId, response.studyReviewId)
        viewModel.add(link)

        responseEntity = ResponseEntity.status(HttpStatus.OK).body(viewModel)
    }

    override fun prepareFailView(throwable: Throwable) {
        responseEntity = createErrorResponseFrom(throwable)
    }

    override fun isDone(): Boolean {
        return responseEntity != null
    }

    data class ViewModel(
        val systematicStudyId: UUID,
        val studyReviewId: Long,
        val succeededAnswers: List<UUID>,
        val failedAnswers: List<FailedAnswer>,
        val totalAnswered: Int
    ) : RepresentationModel<ViewModel>()
}