package br.all.question.presenter.riskOfBias

import br.all.application.question.create.CreateQuestionPresenter
import br.all.application.question.create.CreateQuestionService.*
import br.all.question.controller.ExtractionQuestionController
import br.all.question.controller.RiskOfBiasQuestionController
import br.all.question.presenter.extraction.RestfulCreateExtractionQuestionPresenter
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
        val restfulResponse = ViewModel(response.researcherId, response.systematicStudyId, response.questionId)
    }
        override fun prepareFailView(throwable: Throwable) = run {responseEntity = createErrorResponseFrom(throwable) }
        override fun isDone() = responseEntity != null
        private data class ViewModel(
            val researcherId: UUID,
            val systematicStudyId: UUID,
            val questionId: UUID
        ): RepresentationModel<ViewModel>()

    private fun prepareHateoas(response: ResponseModel, restfulResponse: ViewModel) {
        val self = linkTo<RiskOfBiasQuestionController> {
            findQuestion(response.researcherId, response.systematicStudyId, response.questionId)
        }.withSelfRel()

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

        val findAll = linkTo<RiskOfBiasQuestionController> {
            findAllBySystematicStudyId(response.researcherId, response.systematicStudyId)
        }.withRel("findAll")


        restfulResponse.add(self, createPickList, createLabeledScale, createNumberScale, findAll)
        responseEntity = status(HttpStatus.CREATED).body(restfulResponse)
    }

}
