package br.all.question.presenter.riskOfBias

import br.all.application.question.findAll.FindAllBySystematicStudyIdPresenter
import br.all.application.question.findAll.FindAllBySystematicStudyIdService
import br.all.application.question.repository.QuestionDto
import br.all.question.controller.ExtractionQuestionController
import br.all.question.controller.RiskOfBiasQuestionController
import br.all.question.presenter.extraction.RestfulFindAllExtractionQuestionPresenter
import br.all.shared.error.createErrorResponseFrom
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.*

class RestfulFindAllRoBQuestionPresenter : FindAllBySystematicStudyIdPresenter {
    var responseEntity: ResponseEntity<*>? = null
    override fun prepareSuccessView(response: FindAllBySystematicStudyIdService.ResponseModel) {
        val restfulResponse = ViewModel(response.systematicStudyId, response.questions.size, response.questions)
    }
    override fun prepareFailView(throwable: Throwable) = run { responseEntity = createErrorResponseFrom(throwable) }
    override fun isDone() = responseEntity != null
    private data class ViewModel(
        val systematicStudyId: UUID,
        val size: Int,
        val questions: List<QuestionDto>
    ) : RepresentationModel<ViewModel>()

    private fun prepareHateoas(response: FindAllBySystematicStudyIdService.ResponseModel, restfulResponse:ViewModel) {

        val self = linkTo<RiskOfBiasQuestionController> {
            findAllBySystematicStudyId(response.researcherId, response.systematicStudyId)
        }.withSelfRel()

        val createQuestion = linkTo<RiskOfBiasQuestionController> {
            createTextualQuestion(
                response.researcherId,
                response.systematicStudyId,
                request = RiskOfBiasQuestionController.TextualRequest(
                    "code", "description"
                ))
        }.withRel("createQuestion")

        val createPickList = linkTo<RiskOfBiasQuestionController> {
            createPickListQuestion(
                response.researcherId,
                response.systematicStudyId,
                request = RiskOfBiasQuestionController.PickListRequest(
                    "code", "description", listOf("option1")
                )
            )
        }.withRel("createPickList")

        val createLabeledScale = linkTo<RiskOfBiasQuestionController> {
            createLabeledScaleQuestion(
                response.researcherId,
                response.systematicStudyId,
                request = RiskOfBiasQuestionController.LabeledScaleRequest(
                    "code", "description", mapOf("scale1" to 1)
                )
            )
        }.withRel("createLabeledScale")

        val createNumberScale = linkTo<RiskOfBiasQuestionController> {
            createNumberScaleQuestion(
                response.researcherId,
                response.systematicStudyId,
                request = RiskOfBiasQuestionController.NumberScaleRequest(
                    "code", "description", 0, 0
                )
            )
        }.withRel("createNumberScale")

        restfulResponse.add(self, createQuestion,createPickList,createLabeledScale,createNumberScale)
        responseEntity = ResponseEntity.status(HttpStatus.OK).body(restfulResponse)
    }
}