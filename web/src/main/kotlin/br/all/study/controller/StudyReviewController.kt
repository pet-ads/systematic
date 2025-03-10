package br.all.study.controller

import br.all.application.study.create.CreateStudyReviewService
import br.all.application.study.find.service.*
import br.all.application.study.update.implementation.UpdateStudyReviewExtractionService
import br.all.application.study.update.implementation.UpdateStudyReviewPriorityService
import br.all.application.study.update.implementation.UpdateStudyReviewSelectionService
import br.all.application.study.update.interfaces.AnswerRiskOfBiasQuestionService
import br.all.application.study.update.interfaces.MarkAsDuplicatedService
import br.all.application.study.update.interfaces.UpdateStudyReviewService
import br.all.security.service.AuthenticationInfoService
import br.all.study.presenter.*
import br.all.study.requests.PatchRiskOfBiasAnswerStudyReviewRequest
import br.all.study.requests.PatchStatusStudyReviewRequest
import br.all.study.requests.PostStudyReviewRequest
import br.all.study.requests.PutStudyReviewRequest
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
import br.all.application.study.update.interfaces.MarkAsDuplicatedService.RequestModel as DuplicatedRequest
import br.all.application.study.find.service.FindAllStudyReviewsBySourceService.RequestModel as FindAllBySourceRequest
import br.all.application.study.find.service.FindStudyReviewService.RequestModel as FindOneRequest

@RestController
@RequestMapping("/api/v1/systematic-study/{systematicStudy}")
class StudyReviewController(
    private val createService: CreateStudyReviewService,
    private val updateService: UpdateStudyReviewService,
    private val findAllService: FindAllStudyReviewsService,
    private val findAllBySourceService: FindAllStudyReviewsBySourceService,
    private val findAllBySessionService: FindAllStudyReviewsBySessionService,
    private val findAllByAuthorService: FindAllStudyReviewsByAuthorService,
    private val findOneService: FindStudyReviewService,
    private val updateSelectionService: UpdateStudyReviewSelectionService,
    private val updateExtractionService: UpdateStudyReviewExtractionService,
    private val updateReadingPriorityService: UpdateStudyReviewPriorityService,
    private val markAsDuplicatedService: MarkAsDuplicatedService,
    private val answerRiskOfBiasQuestionService: AnswerRiskOfBiasQuestionService,
    private val authenticationInfoService: AuthenticationInfoService,
    private val linksFactory: LinksFactory

) {

    @PostMapping("/study-review")
    @Operation(summary = "Create a study review in the systematic study")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Success creating study review",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = CreateStudyReviewService.ResponseModel::class)
                )]),
            ApiResponse(responseCode = "400", description = "Fail creating study review - invalid input",
                content = [Content(schema = Schema(hidden = true))]),
            ApiResponse(responseCode = "401", description = "Fail creating study review - unauthenticated user",
                content = [Content(schema = Schema(hidden = true))]),
            ApiResponse(responseCode = "403", description = "Fail creating study review - unauthorized user",
                content = [Content(schema = Schema(hidden = true))]),
        ]
    )
    fun createStudyReview(
        @PathVariable systematicStudy: UUID,
        @RequestBody postRequest: PostStudyReviewRequest
    ): ResponseEntity<*> {
        val presenter = RestfulCreateStudyReviewPresenter(linksFactory)
        val userId = authenticationInfoService.getAuthenticatedUserId()
        val request = postRequest.toRequestModel(userId, systematicStudy)
        createService.createFromStudy(presenter, request)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @GetMapping("/study-review")
    @Operation(summary = "Get all existing studies of a systematic review")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success getting studies of a systematic review, either found all studies or found none",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = FindAllStudyReviewsService.ResponseModel::class)
                )]
            ),
            ApiResponse(responseCode = "401", description = "Fail getting all study reviews - unauthenticated user",
                content = [Content(schema = Schema(hidden = true))]),
            ApiResponse(responseCode = "403", description = "Fail getting all study reviews - unauthorized user",
                content = [Content(schema = Schema(hidden = true))]),
        ]
    )
    fun findAllStudyReviews(
        @PathVariable systematicStudy: UUID,
    ): ResponseEntity<*> {
        val presenter = RestfulFindAllStudyReviewsPresenter(linksFactory)
        val userId = authenticationInfoService.getAuthenticatedUserId()
        val request = FindAllStudyReviewsService.RequestModel(userId, systematicStudy)
        findAllService.findAllFromReview(presenter, request)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @GetMapping("/search-source/{searchSource}")
    @Operation(summary = "Get all existing studies of a systematic review search source")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success getting studies of a systematic review search source, either found all studies or found none",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = FindAllStudyReviewsBySourceService.ResponseModel::class)
                )]
            ),
            ApiResponse(responseCode = "401", description = "Fail getting all study reviews by source - unauthenticated user",
                content = [Content(schema = Schema(hidden = true))]),
            ApiResponse(responseCode = "403", description = "Fail getting all study reviews by source - unauthorized user",
                content = [Content(schema = Schema(hidden = true))]),
        ]
    )
    fun findAllStudyReviewsBySource(
        @PathVariable systematicStudy: UUID,
        @PathVariable searchSource: String,
    ): ResponseEntity<*> {
        val presenter = RestfulFindAllStudyReviewsBySourcePresenter(linksFactory)
        val userId = authenticationInfoService.getAuthenticatedUserId()
        val request = FindAllBySourceRequest(userId, systematicStudy, searchSource)
        findAllBySourceService.findAllFromSearchSession(presenter, request)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @GetMapping("/find-by-search-session/{searchSessionId}")
    @Operation(summary = "Get all existing studies of a systematic review of a given search session")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success getting studies of a systematic review of a given search session, " +
                        "either found all studies or found none",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = FindAllStudyReviewsBySessionService.ResponseModel::class)
                )]
            ),
            ApiResponse(responseCode = "401", description = "Fail getting all study reviews by search session - unauthenticated user",
                content = [Content(schema = Schema(hidden = true))]),
            ApiResponse(responseCode = "403", description = "Fail getting all study reviews by search session - unauthorized user",
                content = [Content(schema = Schema(hidden = true))]),
        ]
    )
    fun findAllStudyReviewsBySession(
        @PathVariable systematicStudy: UUID,
        @PathVariable searchSessionId: UUID,
    ): ResponseEntity<*> {
        val presenter = RestfulFindAllStudyReviewsBySessionPresenter(linksFactory)
        val userId = authenticationInfoService.getAuthenticatedUserId()
        val request = FindAllStudyReviewsBySessionService.RequestModel(userId, systematicStudy, searchSessionId)
        findAllBySessionService.findAllBySearchSession(presenter, request)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @GetMapping("/study-review/author/{author}")
    @Operation(summary = "Get all existing study reviews of a systematic study by author")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success getting study reviews of a systematic study by author, either found studies or found none",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = FindAllStudyReviewsByAuthorService.ResponseModel::class)
                )]
            ),
            ApiResponse(responseCode = "401", description = "Fail getting study reviews by author - unauthenticated user",
                content = [Content(schema = Schema(hidden = true))]),
            ApiResponse(responseCode = "403", description = "Fail getting study reviews by author - unauthorized user",
                content = [Content(schema = Schema(hidden = true))]),
        ]
    )
    fun findAllStudyReviewsByAuthor(
        @PathVariable systematicStudy: UUID,
        @PathVariable author: String
    ): ResponseEntity<*> {
        val presenter = RestfulFindAllStudyReviewsByAuthorPresenter(linksFactory)
        val userId = authenticationInfoService.getAuthenticatedUserId()
        val request = FindAllStudyReviewsByAuthorService.RequestModel(userId, systematicStudy, author)
        findAllByAuthorService.findAllByAuthor(presenter, request)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @GetMapping("/study-review/{studyReview}")
    @Operation(summary = "Get an existing study review of a systematic study")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Success getting a study review of a systematic study",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = FindStudyReviewService.ResponseModel::class)
                )]
            ),
            ApiResponse(responseCode = "401", description = "Fail getting study review - unauthenticated user",
                content = [Content(schema = Schema(hidden = true))]),
            ApiResponse(responseCode = "403", description = "Fail getting study review - unauthorized user",
                content = [Content(schema = Schema(hidden = true))]),
            ApiResponse(
                responseCode = "404",
                description = "Fail getting study review - not found",
                content = [Content(schema = Schema(hidden = true))]
            ),
        ]
    )
    fun findStudyReview(
        @PathVariable systematicStudy: UUID,
        @PathVariable studyReview: Long,
    ): ResponseEntity<*> {
        val presenter = RestfulFindStudyReviewPresenter(linksFactory)
        val userId = authenticationInfoService.getAuthenticatedUserId()
        val request = FindOneRequest(userId, systematicStudy, studyReview)
        findOneService.findOne(presenter, request)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @PutMapping("/study-review/{studyReview}")
    @Operation(summary = "Update an existing study review of a systematic study")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success updating an existing study review of a systematic study",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = UpdateStudyReviewService.ResponseModel::class)
                )]
            ),
            ApiResponse(responseCode = "400", description = "Fail to update an existing study review - invalid status",
                content = [Content(schema = Schema(hidden = true))]),
            ApiResponse(
                responseCode = "401",
                description = "Fail to update an existing study review - unauthenticated user",
                content = [Content(schema = Schema(hidden = true))]
            ),ApiResponse(
                responseCode = "403",
                description = "Fail to update an existing study review - unauthorized user",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Fail to update an existing study review - study not found",
                content = [Content(schema = Schema(hidden = true))]
            ),
        ]
    )
    fun updateStudyReview(
        @PathVariable systematicStudy: UUID,
        @PathVariable studyReview: Long,
        @RequestBody putRequest: PutStudyReviewRequest
    ): ResponseEntity<*> {
        val presenter = RestfulUpdateStudyReviewPresenter(linksFactory)
        val userId = authenticationInfoService.getAuthenticatedUserId()
        val request = putRequest.toRequestModel(userId, systematicStudy, studyReview)
        updateService.updateFromStudy(presenter, request)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @PatchMapping("/study-review/{studyReview}/selection-status")
    @Operation(summary = "Update the selection status of study review")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success updating selection status of study review",
                content = [Content(schema = Schema(hidden = true))]),
            ApiResponse(
                responseCode = "400",
                description = "Fail updating selection status of study review - invalid status",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Fail updating selection status of study review - unauthenticated user",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "403",
                description = "Fail updating selection status of study review - unauthorized user",
                content = [Content(schema = Schema(hidden = true))]
            ),
        ]
    )
    fun updateStudyReviewSelectionStatus(
        @PathVariable systematicStudy: UUID,
        @PathVariable studyReview: Long,
        @RequestBody patchRequest: PatchStatusStudyReviewRequest
    ): ResponseEntity<*> {
        val presenter = RestfulUpdateStudyReviewStatusPresenter(linksFactory)
        val userId = authenticationInfoService.getAuthenticatedUserId()
        val request = patchRequest.toRequestModel(userId, systematicStudy, studyReview)
        updateSelectionService.changeStatus(presenter, request)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @PatchMapping("/study-review/{studyReview}/extraction-status")
    @Operation(summary = "Update a extraction status of study review")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success updating extraction status of study review",
                content = [Content(schema = Schema(hidden = true))]),
            ApiResponse(
                responseCode = "400",
                description = "Fail updating extraction status of study review - invalid status",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Fail updating extraction status of study review - unauthenticated user",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "403",
                description = "Fail updating extraction status of study review - unauthorized user",
                content = [Content(schema = Schema(hidden = true))]
            ),
        ]
    )
    fun updateStudyReviewExtractionStatus(
        @PathVariable systematicStudy: UUID,
        @PathVariable studyReview: Long,
        @RequestBody patchRequest: PatchStatusStudyReviewRequest
    ): ResponseEntity<*> {
        val presenter = RestfulUpdateStudyReviewStatusPresenter(linksFactory)
        val userID = authenticationInfoService.getAuthenticatedUserId()
        val request = patchRequest.toRequestModel(userID, systematicStudy, studyReview)
        updateExtractionService.changeStatus(presenter, request)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @PatchMapping("/study-review/{studyReview}/reading-priority")
    @Operation(summary = "Update the reading priority of study review")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success updating reading priority of study review",
                content = [Content(schema = Schema(hidden = true))]),
            ApiResponse(
                responseCode = "400",
                description = "Fail updating reading priority of study review - invalid status",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Fail updating reading priority of study review - unauthenticated user",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "403",
                description = "Fail updating reading priority of study review - unauthorized user",
                content = [Content(schema = Schema(hidden = true))]
            ),
        ]
    )
    fun updateStudyReviewReadingPriority(
        @PathVariable systematicStudy: UUID,
        @PathVariable studyReview: Long,
        @RequestBody patchRequest: PatchStatusStudyReviewRequest
    ): ResponseEntity<*> {
        val presenter = RestfulUpdateStudyReviewStatusPresenter(linksFactory)
        val userID = authenticationInfoService.getAuthenticatedUserId()
        val request = patchRequest.toRequestModel(userID, systematicStudy, studyReview)
        updateReadingPriorityService.changeStatus(presenter, request)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }


    @PatchMapping("/study-review/{studyReview}/riskOfBias-answer")
    @Operation(summary = "Update the answer of a risk of bias question")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success updating answer to risk of bias question",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = AnswerRiskOfBiasQuestionService.ResponseModel::class)
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Fail updating answer to risk of bias question",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Fail updating answer to risk of bias question - unauthenticated user",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "403",
                description = "Fail updating answer to risk of bias question - unauthorized user",
                content = [Content(schema = Schema(hidden = true))]
            ),
        ]
    )
    fun riskOfBiasAnswer(
        @PathVariable systematicStudy: UUID,
        @PathVariable studyReview: Long,
        @RequestBody patchRequest: PatchRiskOfBiasAnswerStudyReviewRequest<*>,
    ) : ResponseEntity<*> {
        val presenter = RestfulAnswerRiskOfBiasQuestionPresenter(linksFactory)
        val userId = authenticationInfoService.getAuthenticatedUserId()
        val request = patchRequest.toRequestModel(userId, systematicStudy, studyReview)
        answerRiskOfBiasQuestionService.answerQuestion(presenter, request)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @PatchMapping("/study-review/{referenceStudyId}/duplicated")
    @Operation(summary = "Mark multiple existing studies as duplicated in the systematic study")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success marking the studies as duplicated in the systematic study",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = MarkAsDuplicatedService.ResponseModel::class)
                )]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Fail marking studies as duplicated in the systematic study - unauthenticated user",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "403",
                description = "Fail marking studies as duplicated in the systematic study - unauthorized user",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Fail marking studies as duplicated in the systematic study - not found",
                content = [Content(schema = Schema(hidden = true))]
            )
        ]
    )
    fun markAsDuplicated(
        @PathVariable systematicStudy: UUID,
        @PathVariable referenceStudyId: Long,
        @RequestBody duplicatedStudyIds: List<Long>
    ): ResponseEntity<*> {
        val presenter = RestfulMarkAsDuplicatedPresenter(linksFactory)
        val userId = authenticationInfoService.getAuthenticatedUserId()
        val request = DuplicatedRequest(userId, systematicStudy, referenceStudyId, duplicatedStudyIds)
        markAsDuplicatedService.markAsDuplicated(presenter, request)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

}