package br.all.study.controller

import br.all.application.study.create.CreateStudyReviewService
import br.all.application.study.find.service.FindAllStudyReviewsBySourceService
import br.all.application.study.find.service.FindAllStudyReviewsService
import br.all.application.study.find.service.FindStudyReviewService
import br.all.application.study.update.implementation.UpdateStudyReviewExtractionService
import br.all.application.study.update.implementation.UpdateStudyReviewPriorityService
import br.all.application.study.update.implementation.UpdateStudyReviewSelectionService
import br.all.application.study.update.interfaces.MarkAsDuplicatedService
import br.all.application.study.update.interfaces.UpdateStudyReviewService
import br.all.study.presenter.*
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
import br.all.application.study.create.CreateStudyReviewService.RequestModel as CreateRequest
import br.all.application.study.update.interfaces.UpdateStudyReviewService.RequestModel as UpdateRequest
import br.all.application.study.find.service.FindAllStudyReviewsService.RequestModel as FindAllRequest
import br.all.application.study.find.service.FindAllStudyReviewsBySourceService.RequestModel as FindAllBySourceRequest
import br.all.application.study.find.service.FindStudyReviewService.RequestModel as FindOneRequest
import br.all.application.study.update.interfaces.UpdateStudyReviewStatusService.RequestModel as UpdateStatusRequest

@RestController
@RequestMapping("/api/v1/researcher/{researcher}/systematic-study/{systematicStudy}")
class StudyReviewController(
    val createService: CreateStudyReviewService,
    val updateService: UpdateStudyReviewService,
    val findAllService: FindAllStudyReviewsService,
    val findAllBySourceService: FindAllStudyReviewsBySourceService,
    val findOneService: FindStudyReviewService,
    val updateSelectionService: UpdateStudyReviewSelectionService,
    val updateExtractionService: UpdateStudyReviewExtractionService,
    val updateReadingPriorityService: UpdateStudyReviewPriorityService,
    val markAsDuplicatedService: MarkAsDuplicatedService
) {

    @PostMapping("/study-review")
    @Operation(summary = "Create a study review in the systematic study")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Success creating study review"),
            ApiResponse(responseCode = "400", description = "Fail creating study review - invalid input"),
        ]
    )
    fun createStudyReview(
        @PathVariable researcher: UUID,
        @PathVariable systematicStudy: UUID,
        @RequestBody request: CreateRequest
    ): ResponseEntity<*> {
        val presenter = RestfulCreateStudyReviewPresenter()
        createService.createFromStudy(presenter, request)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @PutMapping("/study-review")
    @Operation(summary = "Update an existing study review of a systematic study")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success updating an existing study review of a systematic study"
            ),
            ApiResponse(responseCode = "400", description = "Fail to update an existing study review - invalid status"),
            ApiResponse(
                responseCode = "404",
                description = "Fail to update an existing study review - study not found"
            ),
        ]
    )
    fun updateStudyReview(
        @PathVariable researcher: UUID,
        @PathVariable systematicStudy: UUID,
        @RequestBody request: UpdateRequest
    ): ResponseEntity<*> {
        val presenter = RestfulUpdateStudyReviewPresenter()
        updateService.updateFromStudy(presenter, request)
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
        ]
    )
    fun findAllStudyReviews(
        @PathVariable researcher: UUID,
        @PathVariable systematicStudy: UUID,
    ): ResponseEntity<*> {
        val presenter = RestfulFindAllStudyReviewsPresenter()
        val request = FindAllRequest(researcher, systematicStudy)
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
            )
        ]
    )
    fun findAllStudyReviewsBySource(
        @PathVariable researcher: UUID,
        @PathVariable systematicStudy: UUID,
        @PathVariable searchSource: String,
    ): ResponseEntity<*> {
        val presenter = RestfulFindAllStudyReviewsBySourcePresenter()
        val request = FindAllBySourceRequest(researcher, systematicStudy, searchSource)
        findAllBySourceService.findAllFromSearchSession(presenter, request)
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
            ApiResponse(responseCode = "404", description = "Fail getting study review - not found"),
        ]
    )
    fun findStudyReview(
        @PathVariable researcher: UUID,
        @PathVariable systematicStudy: UUID,
        @PathVariable studyReview: Long,
    ): ResponseEntity<*> {
        val presenter = RestfulFindStudyReviewPresenter()
        val request = FindOneRequest(researcher, systematicStudy, studyReview)
        findOneService.findOne(presenter, request)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @PatchMapping("/study-review/{studyReview}/selection-status")
    @Operation(summary = "Update the selection status of study review")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success updating selection status of study review"),
            ApiResponse(
                responseCode = "400",
                description = "Fail updating selection status of study review - invalid status"
            ),
        ]
    )
    fun updateStudyReviewSelectionStatus(
        @PathVariable researcher: UUID,
        @PathVariable systematicStudy: UUID,
        @PathVariable studyReview: Long,
        @RequestBody request: UpdateStatusRequest
    ): ResponseEntity<*> {
        val presenter = RestfulUpdateStudyReviewStatusPresenter()
        updateSelectionService.changeStatus(presenter, request)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @PatchMapping("/study-review/{studyReview}/extraction-status")
    @Operation(summary = "Update a extraction status of study review")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success updating extraction status of study review"),
            ApiResponse(
                responseCode = "400",
                description = "Fail updating extraction status of study review - invalid status"
            ),
        ]
    )
    fun updateStudyReviewExtractionStatus(
        @PathVariable researcher: UUID,
        @PathVariable systematicStudy: UUID,
        @PathVariable studyReview: Long,
        @RequestBody request: UpdateStatusRequest
    ): ResponseEntity<*> {
        val presenter = RestfulUpdateStudyReviewStatusPresenter()
        updateExtractionService.changeStatus(presenter, request)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @PatchMapping("/study-review/{studyReview}/reading-priority")
    @Operation(summary = "Update the reading priority of study review")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success updating reading priority of study review"),
            ApiResponse(
                responseCode = "400",
                description = "Fail updating reading priority of study review - invalid status"
            ),
        ]
    )
    fun updateStudyReviewReadingPriority(
        @PathVariable researcher: UUID,
        @PathVariable systematicStudy: UUID,
        @PathVariable studyReview: Long,
        @RequestBody request: UpdateStatusRequest
    ): ResponseEntity<*> {
        val presenter = RestfulUpdateStudyReviewStatusPresenter()
        updateReadingPriorityService.changeStatus(presenter, request)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @PatchMapping("/study-review/{studyReviewIdToKeep}/duplicated/{studyReviewToMarkAsDuplicated}")
    @Operation(summary = "Mark an existing study as duplicated in the systematic study")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success marking an existing study as duplicated in the systematic study"
            ),
            ApiResponse(
                responseCode = "404",
                description = "Fail marking an existing study as duplicated in the systematic study - not found"
            ),
        ]
    )
    fun markAsDuplicated(
        @PathVariable researcher: UUID,
        @PathVariable systematicStudy: UUID,
        @PathVariable studyReviewIdToKeep: Long,
        @PathVariable studyReviewToMarkAsDuplicated: Long,
    ): ResponseEntity<*> {
        val presenter = RestfulMarkAsDuplicatedPresenter()
        val request = DuplicatedRequest(researcher, systematicStudy, studyReviewIdToKeep, studyReviewToMarkAsDuplicated)
        markAsDuplicatedService.markAsDuplicated(presenter, request)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }
}