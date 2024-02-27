package br.all.question.presenter.extraction

import br.all.application.question.findAll.FindAllBySystematicStudyIdPresenter
import br.all.application.question.findAll.FindAllBySystematicStudyIdService.ResponseModel
import br.all.application.question.repository.QuestionDto
import br.all.question.controller.ExtractionQuestionController
import br.all.shared.error.createErrorResponseFrom
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.*

class RestfulFindAllExtractionQuestionPresenter : FindAllBySystematicStudyIdPresenter {
    var responseEntity: ResponseEntity<*>? = null
    override fun prepareSuccessView(response: ResponseModel) {
        val restfulResponse = ViewModel(response.systematicStudyId, response.questions.size, response.questions)

        val self = linkTo<ExtractionQuestionController> {
            findAllBySystematicStudyId(response.researcherId, response.systematicStudyId)
        }.withSelfRel()

        restfulResponse.add(self)
        responseEntity = ResponseEntity.status(HttpStatus.OK).body(restfulResponse)
    }

    override fun prepareFailView(throwable: Throwable) = run { responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    private data class ViewModel(
        val systematicStudyId: UUID,
        val size: Int,
        val questions: List<QuestionDto>
    ) : RepresentationModel<ViewModel>()
}