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
        val viewModel = ViewModel(response.researcherId, response.systematicStudyId, response.questionId)

        val selfRef = linkSelfRef(response)
        val pickList = linkCreatePickList(response)
        val labeledScale = linkCreateLabeledScale(response)
        val numberScale = linkCreateNumberScale(response)
        val findAll = linkFindAll(response)

        viewModel.add(selfRef, pickList, labeledScale, numberScale, findAll)
        responseEntity = status(HttpStatus.CREATED).body(viewModel)
    }

    private fun linkSelfRef(response: ResponseModel) =
        linkTo<ExtractionQuestionController> {
            findQuestion(response.researcherId, response.systematicStudyId, response.questionId)
        }.withSelfRel()

    private fun linkCreatePickList(response: ResponseModel) =
        linkTo<ExtractionQuestionController> {
            createPickListQuestion(
                response.researcherId,
                response.systematicStudyId,
                request = ExtractionQuestionController.PickListRequest(
                    "code", "description", listOf("option1")
                )
            )
        }.withRel("pickList")

    private fun linkCreateLabeledScale(response: ResponseModel) =
        linkTo<ExtractionQuestionController> {
            createLabeledScaleQuestion(
                response.researcherId,
                response.systematicStudyId,
                request = ExtractionQuestionController.LabeledScaleRequest(
                    "code", "description", mapOf("scale1" to 1)
                )
            )
        }.withRel("labeledScale")

    private fun linkCreateNumberScale(response: ResponseModel) =
        linkTo<ExtractionQuestionController> {
            createNumberScaleQuestion(
                response.researcherId,
                response.systematicStudyId,
                request = ExtractionQuestionController.NumberScaleRequest(
                    "code", "description", 0, 0
                )
            )
        }.withRel("numberScale")
    private fun linkFindAll(response: ResponseModel) =
        linkTo<ExtractionQuestionController> {
            findAllBySystematicStudyId(response.researcherId, response.systematicStudyId)
        }.withRel("findAll")

    override fun prepareFailView(throwable: Throwable) = run { responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    private data class ViewModel(
        val researcherId: UUID,
        val systematicStudyId: UUID,
        val questionId: UUID
    ) : RepresentationModel<ViewModel>()
}
