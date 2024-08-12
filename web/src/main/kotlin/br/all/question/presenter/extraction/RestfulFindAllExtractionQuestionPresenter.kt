package br.all.question.presenter.extraction

import br.all.application.question.findAll.FindAllBySystematicStudyIdPresenter
import br.all.application.question.findAll.FindAllBySystematicStudyIdService.ResponseModel
import br.all.application.question.repository.QuestionDto
import br.all.question.controller.ExtractionQuestionController
import br.all.shared.error.createErrorResponseFrom
import br.all.utils.LinksFactory
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.*

class RestfulFindAllExtractionQuestionPresenter(
    private val linksFactory: LinksFactory
) : FindAllBySystematicStudyIdPresenter {
    var responseEntity: ResponseEntity<*>? = null
    override fun prepareSuccessView(response: ResponseModel) {
        val viewModel = ViewModel(response.systematicStudyId, response.questions.size, response.questions)

        val selfRef = linksFactory.findAllReviewExtractionQuestions(response.systematicStudyId)
        val createQuestion = linksFactory.createTextualExtractionQuestion(response.systematicStudyId)
        val createPickList = linksFactory.createPickListExtractionQuestion(response.systematicStudyId)
        val createLabeledScale = linksFactory.createLabeledScaleExtractionQuestion(response.systematicStudyId)
        val createNumberScale = linksFactory.createNumberScaleExtractionQuestion(response.systematicStudyId)

        viewModel.add(selfRef, createQuestion, createPickList, createLabeledScale, createNumberScale)
        responseEntity = ResponseEntity.status(HttpStatus.OK).body(viewModel)
    }

    override fun prepareFailView(throwable: Throwable) = run { responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    private data class ViewModel(
        val systematicStudyId: UUID,
        val size: Int,
        val questions: List<QuestionDto>
    ) : RepresentationModel<ViewModel>()

}