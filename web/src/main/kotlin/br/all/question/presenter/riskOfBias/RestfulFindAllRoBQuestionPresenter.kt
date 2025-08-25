package br.all.question.presenter.riskOfBias

import br.all.application.question.findAll.FindAllBySystematicStudyIdPresenter
import br.all.application.question.findAll.FindAllBySystematicStudyIdService
import br.all.application.question.repository.QuestionDto
import br.all.question.controller.RiskOfBiasQuestionController
import br.all.shared.error.createErrorResponseFrom
import br.all.utils.LinksFactory
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.*

class RestfulFindAllRoBQuestionPresenter(
    private val linksFactory: LinksFactory
) : FindAllBySystematicStudyIdPresenter {
    var responseEntity: ResponseEntity<*>? = null
    override fun prepareSuccessView(response: FindAllBySystematicStudyIdService.ResponseModel) {
        val restfulResponse = ViewModel(response.systematicStudyId, response.questions.size, response.questions)

        val selfRef = linksFactory.findAllReviewRobQuestions(response.systematicStudyId)
        val createQuestion = linksFactory.createTextualRobQuestion(response.systematicStudyId)
        val createPickList = linksFactory.createPickListRobQuestion(response.systematicStudyId)
        val createPickMany = linksFactory.createPickManyRobQuestion(response.systematicStudyId)
        val createLabeledScale = linksFactory.createLabeledScaleRobQuestion(response.systematicStudyId)
        val createNumberScale = linksFactory.createNumberScaleRobQuestion(response.systematicStudyId)

        restfulResponse.add(selfRef, createQuestion, createPickList, createPickMany, createLabeledScale, createNumberScale)
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