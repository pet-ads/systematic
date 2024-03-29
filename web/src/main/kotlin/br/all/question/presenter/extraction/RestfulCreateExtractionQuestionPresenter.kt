package br.all.question.presenter.extraction

import br.all.application.question.create.CreateQuestionPresenter
import br.all.application.question.create.CreateQuestionService.*
import br.all.question.controller.ExtractionQuestionController
import br.all.shared.error.createErrorResponseFrom
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.*
import org.springframework.stereotype.Component
import java.util.*

@Component
class RestfulCreateExtractionQuestionPresenter : CreateQuestionPresenter {
    var responseEntity: ResponseEntity<*>? = null
    override fun prepareSuccessView(response: ResponseModel) {
        val restfulResponse = ViewModel(response.researcherId, response.systematicStudyId, response.questionId)

        val self = linkTo<ExtractionQuestionController> {
            findQuestion(response.researcherId, response.systematicStudyId, response.questionId)
        }.withSelfRel()

        restfulResponse.add(self)
        responseEntity = status(HttpStatus.CREATED).body(restfulResponse)
    }

    override fun prepareFailView(throwable: Throwable) = run {responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    private data class ViewModel(
        val researcherId: UUID,
        val systematicStudyId: UUID,
        val questionId: UUID
    ): RepresentationModel<ViewModel>()
}
