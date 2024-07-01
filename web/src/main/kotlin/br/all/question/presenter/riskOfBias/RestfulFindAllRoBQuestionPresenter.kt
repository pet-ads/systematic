package br.all.question.presenter.riskOfBias

import br.all.application.question.findAll.FindAllBySystematicStudyIdPresenter
import br.all.application.question.findAll.FindAllBySystematicStudyIdService
import br.all.application.question.repository.QuestionDto
import br.all.question.controller.RiskOfBiasQuestionController
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

        val selfRef = linkSelfRef(response)
        val createQuestion = linkCreateQuestion(response)
        val createPickList = linkCreatePickList(response)
        val createLabeledScale = linkCreateLabeledScale(response)
        val createNumberScale = linkCreateNumberScale(response)

        restfulResponse.add(selfRef, createQuestion ,createPickList,createLabeledScale,createNumberScale)
        responseEntity = ResponseEntity.status(HttpStatus.OK).body(restfulResponse)
    }


    private fun linkSelfRef(response: FindAllBySystematicStudyIdService.ResponseModel) =
        linkTo<RiskOfBiasQuestionController> {
            findAllBySystematicStudyId(response.systematicStudyId)
        }.withSelfRel()



    private fun linkCreateQuestion(response: FindAllBySystematicStudyIdService.ResponseModel) =
        linkTo<RiskOfBiasQuestionController> {
            createTextualQuestion(
                response.systematicStudyId,
                request = RiskOfBiasQuestionController.TextualRequest(
                    "code", "description"
                )
            )
        }.withRel("create-textual-rob-question")


    private fun linkCreatePickList(response: FindAllBySystematicStudyIdService.ResponseModel) =
        linkTo<RiskOfBiasQuestionController> {
            createPickListQuestion(
                response.systematicStudyId,
                request = RiskOfBiasQuestionController.PickListRequest(
                    "code", "description", listOf("option1")
                )
            )
        }.withRel("create-pick-list-rob-question")


    private fun linkCreateLabeledScale(response: FindAllBySystematicStudyIdService.ResponseModel) =
        linkTo<RiskOfBiasQuestionController> {
            createLabeledScaleQuestion(
                response.systematicStudyId,
                request = RiskOfBiasQuestionController.LabeledScaleRequest(
                    "code", "description", mapOf("scale1" to 1)
                )
            )
        }.withRel("create-labeled-scale-rob-question")


    private fun linkCreateNumberScale(response: FindAllBySystematicStudyIdService.ResponseModel) =
        linkTo<RiskOfBiasQuestionController> {
            createNumberScaleQuestion(
                response.systematicStudyId,
                request = RiskOfBiasQuestionController.NumberScaleRequest(
                    "code", "description", 0, 0
                )
            )
        }.withRel("create-numberScale-rob-question")


    override fun prepareFailView(throwable: Throwable) = run { responseEntity = createErrorResponseFrom(throwable) }
    override fun isDone() = responseEntity != null
    private data class ViewModel(
        val systematicStudyId: UUID,
        val size: Int,
        val questions: List<QuestionDto>
    ) : RepresentationModel<ViewModel>()
}