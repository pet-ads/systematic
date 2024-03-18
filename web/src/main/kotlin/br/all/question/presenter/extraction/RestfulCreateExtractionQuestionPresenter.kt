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
    }

    override fun prepareFailView(throwable: Throwable) = run { responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    private data class ViewModel(
        val researcherId: UUID,
        val systematicStudyId: UUID,
        val questionId: UUID
    ) : RepresentationModel<ViewModel>()

    private fun prepareHateoas(response: ResponseModel, restfulResponse: ViewModel) {
        val self = linkTo<ExtractionQuestionController> {
            findQuestion(response.researcherId, response.systematicStudyId, response.questionId)
        }.withSelfRel()

        val createPickList = linkTo<ExtractionQuestionController> {
            createPickListQuestion(
                response.researcherId,
                response.systematicStudyId,
                request = ExtractionQuestionController.PickListRequest(
                    "code", "description", listOf("option1")
                )
            )
        }.withRel("createPickList")

        val createLabeledScale = linkTo<ExtractionQuestionController> {
            createLabeledScaleQuestion(
                response.researcherId,
                response.systematicStudyId,
                request = ExtractionQuestionController.LabeledScaleRequest(
                    "code", "description", mapOf("scale1" to 1)
                )
            )
        }.withRel("createLabeledScale")

        val createNumberScale = linkTo<ExtractionQuestionController> {
            createNumberScaleQuestion(
                response.researcherId,
                response.systematicStudyId,
                request = ExtractionQuestionController.NumberScaleRequest(
                    "code", "description", 0, 0
                )
            )
        }.withRel("createNumberScale")

        val findAll = linkTo<ExtractionQuestionController> {
            findAllBySystematicStudyId(response.researcherId, response.systematicStudyId)
        }.withRel("findAll")


        restfulResponse.add(self, createPickList, createLabeledScale, createNumberScale, findAll)
        responseEntity = status(HttpStatus.CREATED).body(restfulResponse)
    }
}
