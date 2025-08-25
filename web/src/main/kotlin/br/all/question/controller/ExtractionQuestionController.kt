package br.all.question.controller

import br.all.application.question.create.CreateQuestionService
import br.all.application.question.create.CreateQuestionService.QuestionType.*
import br.all.application.question.create.CreateQuestionService.RequestModel
import br.all.application.question.find.FindQuestionService
import br.all.application.question.findAll.FindAllBySystematicStudyIdService
import br.all.application.question.update.services.UpdateQuestionService
import br.all.application.question.findAll.FindAllBySystematicStudyIdService.RequestModel as FindAllRequest
import br.all.question.presenter.extraction.RestfulFindExtractionQuestionPresenter
import br.all.question.presenter.extraction.RestfulCreateExtractionQuestionPresenter
import br.all.question.presenter.extraction.RestfulFindAllExtractionQuestionPresenter
import br.all.question.presenter.extraction.RestfulUpdateExtractionQuestionPresenter
import br.all.question.requests.PutRequest
import br.all.security.service.AuthenticationInfoService
import br.all.utils.LinksFactory
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import br.all.application.question.create.CreateQuestionService.RequestModel as CreateRequest

@RestController
@RequestMapping("/api/v1/systematic-study/{systematicStudyId}/protocol/extraction-question")
class ExtractionQuestionController(
    val authenticationInfoService: AuthenticationInfoService,
    val createQuestionService: CreateQuestionService,
    val findOneService: FindQuestionService,
    val findAllService: FindAllBySystematicStudyIdService,
    val updateQuestionService: UpdateQuestionService,
    val linksFactory: LinksFactory
) {
    data class TextualRequest(val code: String, val description: String)
    data class PickListRequest(val code: String, val description: String, val options: List<String>)
    data class PickManyRequest(val code: String, val description: String, val options: List<String>)
    data class LabeledScaleRequest(val code: String, val description: String, val scales: Map<String, Int>)
    data class NumberScaleRequest(val code: String, val description: String, val lower: Int, val higher: Int)
    val questionContext = "EXTRACTION"

    fun createQuestion(request: CreateRequest): ResponseEntity<*> {
        val presenter = RestfulCreateExtractionQuestionPresenter(linksFactory)
        createQuestionService.create(presenter, request)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @PostMapping("/textual")
    @Operation(summary = "Create a extraction textual question in the protocol")
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "Success creating a textual question in the protocol",
            content = [Content(schema = Schema(hidden = true))]),
        ApiResponse(
            responseCode = "400",
            description = "Fail creating a textual question in the protocol - invalid input",
            content = [Content(schema = Schema(hidden = true))]
        ),
        ApiResponse(
            responseCode = "401",
            description = "Fail creating a textual question in the protocol - unauthenticated user",
            content = [Content(schema = Schema(hidden = true))]
        ),
        ApiResponse(
            responseCode = "403",
            description = "Fail creating a textual question in the protocol - unauthorized user",
            content = [Content(schema = Schema(hidden = true))]
        ),
    ]
    )
    fun createTextualQuestion(
        @PathVariable systematicStudyId: UUID,
        @RequestBody request: TextualRequest
    ): ResponseEntity<*> = createQuestion(
        RequestModel(
            authenticationInfoService.getAuthenticatedUserId(),
            systematicStudyId,
            questionContext,
            TEXTUAL,
            request.code,
            request.description,
        )
    )

    @PostMapping("/pick-list")
    @Operation(summary = "Create a extraction pick-list question in the protocol")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Success creating a pick-list question in the protocol",
                content = [Content(schema = Schema(hidden = true))]),
            ApiResponse(
                responseCode = "400",
                description = "Fail creating a pick-list question in the protocol - invalid input",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Fail creating a pick-list question in the protocol - unauthenticated user",
                content = [Content(schema = Schema(hidden = true))]
            ),ApiResponse(
                responseCode = "403",
                description = "Fail creating a pick-list question in the protocol - unauthorized user",
                content = [Content(schema = Schema(hidden = true))]
            ),
        ]
    )
    fun createPickListQuestion(
        @PathVariable systematicStudyId: UUID, @RequestBody request: PickListRequest,
    ): ResponseEntity<*> = createQuestion(
        RequestModel(
            authenticationInfoService.getAuthenticatedUserId(),
            systematicStudyId,
            questionContext,
            PICK_LIST,
            request.code,
            request.description,
            options = request.options
        )
    )

    @PostMapping("/pick-many")
    @Operation(summary = "Create a extraction pick-many question in the protocol")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Success creating a pick-many question in the protocol",
                content = [Content(schema = Schema(hidden = true))]),
            ApiResponse(
                responseCode = "400",
                description = "Fail creating a pick-many question in the protocol - invalid input",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Fail creating a pick-many question in the protocol - unauthenticated user",
                content = [Content(schema = Schema(hidden = true))]
            ),ApiResponse(
                responseCode = "403",
                description = "Fail creating a pick-many question in the protocol - unauthorized user",
                content = [Content(schema = Schema(hidden = true))]
            ),
        ]
    )
    fun createPickManyQuestion(
        @PathVariable systematicStudyId: UUID, @RequestBody request: PickManyRequest,
    ): ResponseEntity<*> = createQuestion(
        RequestModel(
            authenticationInfoService.getAuthenticatedUserId(),
            systematicStudyId,
            questionContext,
            PICK_MANY,
            request.code,
            request.description,
            options = request.options
        )
    )

    @PostMapping("/labeled-scale")
    @Operation(summary = "Create a extraction labeled-scale question in the protocol")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Success creating a labeled-scale question in the protocol",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Fail creating a labeled-scale question in the protocol - invalid input",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Fail creating a labeled-scale question in the protocol - unauthenticated user",
                content = [Content(schema = Schema(hidden = true))]
            ),ApiResponse(
                responseCode = "403",
                description = "Fail creating a labeled-scale question in the protocol - unauthorized user",
                content = [Content(schema = Schema(hidden = true))]
            ),
        ]
    )
    fun createLabeledScaleQuestion(
        @PathVariable systematicStudyId: UUID,
        @RequestBody request: LabeledScaleRequest,
    ): ResponseEntity<*> = createQuestion(
        RequestModel(
            authenticationInfoService.getAuthenticatedUserId(),
            systematicStudyId,
            questionContext,
            LABELED_SCALE,
            request.code,
            request.description,
            scales = request.scales
        )
    )

    @PostMapping("/number-scale")
    @Operation(summary = "Create a extraction number-scale question in the protocol")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Success creating a number-scale question in the protocol",
                content = [Content(schema = Schema(hidden = true))]),
            ApiResponse(
                responseCode = "400",
                description = "Fail creating a number-scale question in the protocol - invalid input",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Fail creating a number-scale question in the protocol - unauthenticated user",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "403",
                description = "Fail creating a number-scale question in the protocol - unauthorized user",
                content = [Content(schema = Schema(hidden = true))]
            ),
        ]
    )
    fun createNumberScaleQuestion(
        @PathVariable systematicStudyId: UUID,
        @RequestBody request: NumberScaleRequest,
    ): ResponseEntity<*> = createQuestion(
        RequestModel(
            authenticationInfoService.getAuthenticatedUserId(),
            systematicStudyId,
            questionContext,
            NUMBERED_SCALE,
            request.code,
            request.description,
            higher = request.higher,
            lower = request.lower
        )
    )

    @GetMapping("/{questionId}")
    @Operation(summary = "Get an extraction question of a given protocol by code")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success getting an extraction question of a given protocol by code",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = FindQuestionService.ResponseModel::class)
                )]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Fail getting an extraction question of a given protocol by code - not found",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Fail getting an extraction question of a given protocol by code - unauthenticated user",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "403",
                description = "Fail getting an extraction question of a given protocol by code - unauthorized user",
                content = [Content(schema = Schema(hidden = true))]
            ),
        ]
    )
    fun findQuestion(
        @PathVariable systematicStudyId: UUID,
        @PathVariable questionId: UUID,
    ): ResponseEntity<*> {
        val presenter = RestfulFindExtractionQuestionPresenter(linksFactory)
        val request = FindQuestionService.RequestModel(
            authenticationInfoService.getAuthenticatedUserId(),
            systematicStudyId,
            questionId,
            questionContext
        )
        findOneService.findOne(presenter, request)

        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @GetMapping
    @Operation(summary = "Get all extraction questions in the protocol")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success getting all extraction questions in the protocol. Either returns the questions of a systematic study or an empty list if no systematic study is found.",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = FindAllBySystematicStudyIdService.ResponseModel::class)
                )]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Fail getting all extraction questions in the protocol - unauthenticated user",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "403",
                description = "Fail getting all extraction questions in the protocol - unauthorized user",
                content = [Content(schema = Schema(hidden = true))]
            ),
        ]
    )
    fun findAllBySystematicStudyId(
        @PathVariable systematicStudyId: UUID
    ): ResponseEntity<*> {
        val presenter = RestfulFindAllExtractionQuestionPresenter(linksFactory)
        val request = FindAllRequest(
            userId = authenticationInfoService.getAuthenticatedUserId(),
            systematicStudyId = systematicStudyId,
            questionContext
        )
        findAllService.findAllBySystematicStudyId(presenter, request)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }


    @PutMapping("/{questionId}")
    @Operation(summary = "Update a extraction question")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success updating a extraction question",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = UpdateQuestionService.ResponseModel::class)
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Fail updating question - invalid request",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Fail updating question - unauthenticated user",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "403",
                description = "Fail updating question - unauthorized user",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Fail updating question - question not found",
                content = [Content(schema = Schema(hidden = true))]
            ),
        ]
    )
    fun updateQuestion(
        @PathVariable systematicStudyId: UUID,
        @PathVariable questionId: UUID,
        @RequestBody request: PutRequest
    ): ResponseEntity<*> {
        val presenter = RestfulUpdateExtractionQuestionPresenter(linksFactory)
        val userId = authenticationInfoService.getAuthenticatedUserId()
        val requestModel = request.toUpdateRequestModel(userId, systematicStudyId, questionId)

        updateQuestionService.update(presenter, requestModel)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }
}