package br.all.question.presenter.extraction

import br.all.application.question.create.CreateQuestionService
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
        val viewModel = ViewModel(response.systematicStudyId, response.questions.size, response.questions)

        val selfRef = linkSelfRef(response)
        val createQuestion = linkCreateQuestion(response)
        val createPickList = linkCreatePickList(response)
        val createLabeledScale = linkCreateLabeledScale(response)
        val createNumberScale = linkCreateNumberScale(response)

        viewModel.add(selfRef, createQuestion, createPickList, createLabeledScale, createNumberScale)
        responseEntity = ResponseEntity.status(HttpStatus.OK).body(viewModel)
    }

    private fun linkSelfRef(response: ResponseModel) =
        linkTo<ExtractionQuestionController> {
            findAllBySystematicStudyId(response.researcherId, response.systematicStudyId)
        }.withSelfRel()

    private fun linkCreateQuestion(response: ResponseModel) =
        linkTo<ExtractionQuestionController> {
            createTextualQuestion(
                response.researcherId,
                response.systematicStudyId,
                request = ExtractionQuestionController.TextualRequest(
                    "code", "description"
                )
            )
        }.withRel("createQuestion")

    private fun linkCreatePickList(response: ResponseModel) =
        linkTo<ExtractionQuestionController> {
            createPickListQuestion(
                response.researcherId,
                response.systematicStudyId,
                request = ExtractionQuestionController.PickListRequest(
                    "code", "description", listOf("option1")
                )
            )
        }.withRel("createPickList")

    private fun linkCreateLabeledScale(response: ResponseModel) =
        linkTo<ExtractionQuestionController> {
            createLabeledScaleQuestion(
                response.researcherId,
                response.systematicStudyId,
                request = ExtractionQuestionController.LabeledScaleRequest(
                    "code", "description", mapOf("scale1" to 1)
                )
            )
        }.withRel("createLabeledScale")

    private fun linkCreateNumberScale(response: ResponseModel) =
        linkTo<ExtractionQuestionController> {
            createNumberScaleQuestion(
                response.researcherId,
                response.systematicStudyId,
                request = ExtractionQuestionController.NumberScaleRequest(
                    "code", "description", 0, 0
                )
            )
        }.withRel("createNumberScale")


    override fun prepareFailView(throwable: Throwable) = run { responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    private data class ViewModel(
        val systematicStudyId: UUID,
        val size: Int,
        val questions: List<QuestionDto>
    ) : RepresentationModel<ViewModel>()

}