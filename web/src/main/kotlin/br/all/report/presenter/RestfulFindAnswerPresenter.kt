package br.all.report.presenter

import br.all.application.question.repository.QuestionDto
import br.all.application.report.find.presenter.FindAnswerPresenter
import br.all.application.report.find.service.FindAnswerService
import br.all.shared.error.createErrorResponseFrom
import br.all.utils.LinksFactory
import org.springframework.hateoas.RepresentationModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.*

class RestfulFindAnswerPresenter(
    private val linksFactory: LinksFactory
): FindAnswerPresenter {
    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: FindAnswerService.ResponseModel) {
        val restfulResponse = ViewModel(
            userId = response.userId,
            systematicStudyId = response.systematicStudyId,
            question = response.question,
            answer = response.answer
        )

        val selfRef = linksFactory.findAnswer(response.systematicStudyId, response.question.questionId)

        restfulResponse.add(selfRef)
        responseEntity = ResponseEntity.status(HttpStatus.OK).body(restfulResponse)
    }

    override fun prepareFailView(throwable: Throwable) = run {responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    data class ViewModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val question: QuestionDto,
        val answer: Map<String, List<Long>>
    ): RepresentationModel<ViewModel>()
}