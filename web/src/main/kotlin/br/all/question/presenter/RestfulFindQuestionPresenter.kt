package br.all.question.presenter

import br.all.application.question.create.QuestionDTO
import br.all.application.question.find.FindQuestionPresenter
import br.all.application.question.find.FindQuestionService.*
import br.all.domain.model.question.QuestionId
import br.all.question.controller.QuestionController
import br.all.shared.error.createErrorResponseFrom
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

@Component
class RestfulFindQuestionPresenter : FindQuestionPresenter {
    var responseEntity: ResponseEntity<*>? = null
    override fun prepareSuccessView(response: ResponseModel) {
        val restfulResponse = ViewModel(response.content)

        val self = linkTo<QuestionController> {
            findQuestion(response.researcherId, response.content.systematicStudyId, QuestionId(response.content.questionId))
        }.withSelfRel()

        restfulResponse.add(self)
        responseEntity = ResponseEntity.status(HttpStatus.OK).body(restfulResponse)
    }

    override fun prepareFailView(throwable: Throwable) = run { responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    private data class ViewModel(private val content: QuestionDTO) : RepresentationModel<ViewModel>(){
        val systematicStudyId = content.systematicStudyId
        val questionId = content.questionId
        val protocolId = content.protocolId
        val code = content.code
        val description = content.code
        val questionType = content.questionType
        val scales = content.scales
        val higher = content.higher
        val lower = content.lower
        val options = content.options
    }

}
