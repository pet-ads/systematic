package br.all.question.presenter.riskOfBias

import br.all.application.question.create.CreateQuestionPresenter
import br.all.application.question.create.CreateQuestionService.*
import br.all.question.controller.RiskOfBiasQuestionController
import br.all.shared.error.createErrorResponseFrom
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.*
import org.springframework.stereotype.Component
import java.util.*

@Component
class RestfulCreateRoBQuestionPresenter : CreateQuestionPresenter {
    var responseEntity: ResponseEntity<*>? = null
    override fun prepareSuccessView(response: ResponseModel) {
        val restfulResponse = ViewModel(response.userId, response.systematicStudyId, response.questionId)

        val selfRef = linkSelfRef(response)
        val pickList = linkCreatePickList(response)
        val labeledScale = linkCreateLabeledScale(response)
        val numberScale = linkCreateNumberScale(response)
        val findAll = linkFindAll(response)

        restfulResponse.add(selfRef, pickList, labeledScale, numberScale, findAll)
        responseEntity = status(HttpStatus.CREATED).body(restfulResponse)
    }


    private fun linkSelfRef(response: ResponseModel) =
        linkTo<RiskOfBiasQuestionController> {
        findQuestion(response.systematicStudyId, response.questionId)
    }.withSelfRel()


    private fun linkCreatePickList(response: ResponseModel) =
        linkTo<RiskOfBiasQuestionController> {
        createPickListQuestion(
            response.systematicStudyId,
            request = RiskOfBiasQuestionController.PickListRequest(
                "code", "description", listOf("option1")
            )
        )
    }.withRel("create-pick-list-rob-question")

    private fun linkCreateLabeledScale(response: ResponseModel) =
        linkTo<RiskOfBiasQuestionController> {
        createLabeledScaleQuestion(
            response.systematicStudyId,
            request = RiskOfBiasQuestionController.LabeledScaleRequest(
                "code", "description", mapOf("scale1" to 1)
            )
        )
    }.withRel("create-labeled-scale-rob-question")


    private fun linkCreateNumberScale(response: ResponseModel) =
        linkTo<RiskOfBiasQuestionController> {
        createNumberScaleQuestion(
            response.systematicStudyId,
            request = RiskOfBiasQuestionController.NumberScaleRequest(
                "code", "description", 0, 0
            )
        )
    }.withRel("create-numberScale-rob-question")

    private fun linkFindAll(response: ResponseModel) =
        linkTo<RiskOfBiasQuestionController> {
        findAllBySystematicStudyId(response.systematicStudyId)
    }.withRel("find-all-review-rob-questions")

    override fun prepareFailView(throwable: Throwable) = run {responseEntity = createErrorResponseFrom(throwable) }
    override fun isDone() = responseEntity != null
    private data class ViewModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val questionId: UUID
    ): RepresentationModel<ViewModel>()


}
