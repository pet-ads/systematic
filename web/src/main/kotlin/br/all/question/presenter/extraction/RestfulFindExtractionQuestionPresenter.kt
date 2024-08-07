package br.all.question.presenter.extraction

import br.all.application.question.repository.QuestionDto
import br.all.application.question.find.FindQuestionPresenter
import br.all.application.question.find.FindQuestionService.*
import br.all.question.controller.ExtractionQuestionController
import br.all.shared.error.createErrorResponseFrom
import br.all.utils.LinksFactory
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

@Component
class RestfulFindExtractionQuestionPresenter(
    private val linksFactory: LinksFactory
) : FindQuestionPresenter {
    var responseEntity: ResponseEntity<*>? = null
    override fun prepareSuccessView(response: ResponseModel) {
        val content = response.content
        val restfulResponse = ViewModel(content)

        val self = linksFactory.findExtractionQuestion(content.systematicStudyId, content.questionId)
        restfulResponse.add(self)
        responseEntity = ResponseEntity.status(HttpStatus.OK).body(restfulResponse)
    }

    override fun prepareFailView(throwable: Throwable) = run { responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    private data class ViewModel(private val content: QuestionDto) : RepresentationModel<ViewModel>(){
        val questionId = content.questionId
        val systematicStudyId = content.systematicStudyId
        val code = content.code
        val description = content.code
        val questionType = content.questionType
        val scales = content.scales
        val higher = content.higher
        val lower = content.lower
        val options = content.options
    }

}
