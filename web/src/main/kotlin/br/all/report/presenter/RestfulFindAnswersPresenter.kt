package br.all.report.presenter

import br.all.application.report.find.presenter.FindAnswersPresenter
import br.all.application.report.find.service.FindAnswersService.ResponseModel
import br.all.shared.error.createErrorResponseFrom
import br.all.utils.LinksFactory
import org.springframework.hateoas.RepresentationModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class RestfulFindAnswersPresenter(
    private val linksFactory: LinksFactory
): FindAnswersPresenter {
    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ResponseModel<*>) {
        val restfulFindAnswersResponse = ViewModel(
            response.type,
            response.description,
            response.code,
            response.answer
        )

        val selfRef = linksFactory.findAnswers(response.systematicStudyId, response.questionId, response.studyReviewId,)
        val findQuestion = linksFactory.findRobQuestion(response.systematicStudyId, response.questionId) // TODO(Figure out how to output the link to the correct type of question)

        restfulFindAnswersResponse.add(selfRef, findQuestion)
        responseEntity = ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body(restfulFindAnswersResponse)
    }

    override fun prepareFailView(throwable: Throwable) = run {responseEntity = createErrorResponseFrom(throwable)}

    override fun isDone() = responseEntity != null

    data class ViewModel<T>(
        val type: String,
        val description: String,
        val code: String,
        val answer: T,
    ): RepresentationModel<ViewModel<T>>()
}