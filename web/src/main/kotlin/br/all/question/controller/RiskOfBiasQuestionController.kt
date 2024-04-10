package br.all.question.controller

import br.all.application.question.create.CreateQuestionService
import br.all.application.question.create.CreateQuestionService.QuestionType.*
import br.all.application.question.create.CreateQuestionService.RequestModel
import br.all.application.question.find.FindQuestionService
import br.all.application.question.findAll.FindAllBySystematicStudyIdService
import br.all.question.presenter.extraction.RestfulFindAllExtractionQuestionPresenter
import br.all.question.presenter.riskOfBias.RestfulCreateRoBQuestionPresenter
import br.all.question.presenter.riskOfBias.RestfulFindRoBQuestionPresenter
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.HttpStatus
import br.all.application.question.create.CreateQuestionService.RequestModel as CreateRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/researcher/{researcherId}/systematic-study/{systematicStudyId}/protocol/rob-question")
class RiskOfBiasQuestionController(
    val createQuestionService: CreateQuestionService,
    val findOneService: FindQuestionService,
    val findAllService: FindAllBySystematicStudyIdService
) {
    data class TextualRequest(val code: String, val description: String)
    data class PickListRequest(val code: String, val description: String, val options: List<String>)
    data class LabeledScaleRequest(val code: String, val description: String, val scales: Map<String, Int>)
    data class NumberScaleRequest(val code: String, val description: String, val lower: Int, val higher: Int)

    fun createQuestion(request: CreateRequest): ResponseEntity<*> {
        val presenter = RestfulCreateRoBQuestionPresenter()
        createQuestionService.create(presenter, request)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @PostMapping("/textual")
    @Operation(summary = "Create a risk of bias textual question in the protocol")
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "Success creating a textual question in the protocol"),
        ApiResponse(responseCode = "400", description = "Fail creating a textual question in the protocol - invalid input"),
    ])
    fun createTextualQuestion(
        @PathVariable researcherId: UUID, @PathVariable systematicStudyId: UUID, @RequestBody request: TextualRequest,
    ): ResponseEntity<*> = createQuestion(
        RequestModel(
            researcherId,
            systematicStudyId,
            TEXTUAL,
            request.code,
            request.description
        )
    )

    @PostMapping("/pick-list")
    @Operation(summary = "Create a risk of bias pick-list question in the protocol")
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "Success creating a pick-list question in the protocol"),
        ApiResponse(responseCode = "400", description = "Fail creating a pick-list question in the protocol - invalid input"),
    ])
    fun createPickListQuestion(
        @PathVariable researcherId: UUID, @PathVariable systematicStudyId: UUID, @RequestBody request: PickListRequest,
    ): ResponseEntity<*> = createQuestion(
        RequestModel(
            researcherId,
            systematicStudyId,
            PICK_LIST,
            request.code,
            request.description,
            options = request.options
        )
    )

    @PostMapping("/labeled-scale")
    @Operation(summary = "Create a risk of bias labeled-scale question in the protocol")
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "Success creating a labeled-scale question in the protocol"),
        ApiResponse(responseCode = "400", description = "Fail creating a labeled-scale question in the protocol - invalid input"),
    ])
    fun createLabeledScaleQuestion(
        @PathVariable researcherId: UUID,
        @PathVariable systematicStudyId: UUID,
        @RequestBody request: LabeledScaleRequest,
    ): ResponseEntity<*> = createQuestion(
        RequestModel(
            researcherId,
            systematicStudyId,
            LABELED_SCALE,
            request.code,
            request.description,
            scales = request.scales
        )
    )

    @PostMapping("/number-scale")
    @Operation(summary = "Create a risk of bias number-scale question in the protocol")
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "Success creating a number-scale question in the protocol"),
        ApiResponse(responseCode = "400", description = "Fail creating a number-scale question in the protocol - invalid input"),
    ])
    fun createNumberScaleQuestion(
        @PathVariable researcherId: UUID,
        @PathVariable systematicStudyId: UUID,
        @RequestBody request: NumberScaleRequest,
    ): ResponseEntity<*> = createQuestion(
        RequestModel(
            researcherId,
            systematicStudyId,
            NUMBERED_SCALE,
            request.code,
            request.description,
            higher = request.higher,
            lower = request.lower
        )
    )

    @GetMapping("/{questionId}")
    @Operation(summary = "Get an extraction question of a given protocol by code")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Success getting an risk of bias question of a given protocol by code"),
        ApiResponse(responseCode = "404", description = "Fail getting an risk of bias question of a given protocol by code - not found"),
    ])
    fun findQuestion(
        @PathVariable researcherId: UUID,
        @PathVariable systematicStudyId: UUID,
        @PathVariable questionId: UUID,
    ): ResponseEntity<*> {
        val presenter = RestfulFindRoBQuestionPresenter()
        val request = FindQuestionService.RequestModel(researcherId, systematicStudyId, questionId)
        findOneService.findOne(presenter, request)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @GetMapping
    @Operation(summary = "Get all risk of bias questions in the protocol")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Success getting all risk of bias questions in the protocol. Either returns the questions of a systematic study or an empty list if no systematic study is found."),
    ])
    fun findAllBySystematicStudyId(
        @PathVariable researcherId: UUID,
        @PathVariable systematicStudyId: UUID
    ): ResponseEntity<*> {
        val presenter = RestfulFindAllExtractionQuestionPresenter()
        val request = FindAllBySystematicStudyIdService.RequestModel(researcherId, systematicStudyId)
        findAllService.findAllBySystematicStudyId(presenter, request)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
