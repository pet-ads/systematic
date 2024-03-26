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
        val restfulResponse = ViewModel(response.systematicStudyId, response.questions.size, response.questions)
    }

    override fun prepareFailView(throwable: Throwable) = run { responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    private data class ViewModel(
        val systematicStudyId: UUID,
        val size: Int,
        val questions: List<QuestionDto>
    ) : RepresentationModel<ViewModel>()

    private fun prepareHateoas(response: ResponseModel, restfulResponse: ViewModel) {
        val self = linkTo<ExtractionQuestionController> {
            findAllBySystematicStudyId(response.researcherId, response.systematicStudyId)
        }.withSelfRel()

        val createQuestion = linkTo<ExtractionQuestionController> {
            createTextualQuestion(
                response.researcherId,
                response.systematicStudyId,
                request = ExtractionQuestionController.TextualRequest(
                "code", "description"
            ))
        }.withRel("createQuestion")


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

        restfulResponse.add(self, createQuestion,createPickList,createLabeledScale,createNumberScale)
        responseEntity = ResponseEntity.status(HttpStatus.OK).body(restfulResponse)

    }

}