package br.all.reviewmode.controller

import br.all.application.reviewmode.define.DefineReviewModeService
import br.all.application.reviewmode.find.FindReviewModeService
import br.all.reviewmode.presenter.RestfulDefineReviewModePresenter
import br.all.reviewmode.presenter.RestfulFindReviewModePresenter
import br.all.reviewmode.requests.PatchReviewModeRequest
import br.all.security.service.AuthenticationInfoService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/systematic-study/{systematicStudyId}/review-mode")
class ReviewModeController(
    private val defineReviewModeService: DefineReviewModeService,
    private val findReviewModeService: FindReviewModeService,
    private val authenticationInfoService: AuthenticationInfoService,
) {

    @GetMapping
    @Operation(summary = "Get the review mode of a systematic study stage")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success getting the review mode",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = FindReviewModeService.ResponseModel::class),
                )],
            ),
            ApiResponse(
                responseCode = "401",
                description = "Fail getting the review mode - unauthenticated user",
                content = [Content(schema = Schema(hidden = true))],
            ),
            ApiResponse(
                responseCode = "403",
                description = "Fail getting the review mode - unauthorized user",
                content = [Content(schema = Schema(hidden = true))],
            ),
            ApiResponse(
                responseCode = "404",
                description = "Fail getting the review mode - systematic study not found",
                content = [Content(schema = Schema(hidden = true))],
            ),
        ],
    )
    fun findReviewMode(@PathVariable systematicStudyId: UUID): ResponseEntity<*> {
        val presenter = RestfulFindReviewModePresenter()
        val userId = authenticationInfoService.getAuthenticatedUserId()
        val request = FindReviewModeService.RequestModel(userId, systematicStudyId)

        findReviewModeService.find(presenter, request)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @PatchMapping
    @Operation(summary = "Define the review mode of a systematic study stage")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success defining the review mode",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = DefineReviewModeService.ResponseModel::class),
                )],
            ),
            ApiResponse(
                responseCode = "400",
                description = "Fail defining the review mode - invalid request",
                content = [Content(schema = Schema(hidden = true))],
            ),
            ApiResponse(
                responseCode = "401",
                description = "Fail defining the review mode - unauthenticated user",
                content = [Content(schema = Schema(hidden = true))],
            ),
            ApiResponse(
                responseCode = "403",
                description = "Fail defining the review mode - unauthorized user",
                content = [Content(schema = Schema(hidden = true))],
            ),
            ApiResponse(
                responseCode = "404",
                description = "Fail defining the review mode - systematic study not found",
                content = [Content(schema = Schema(hidden = true))],
            ),
        ],
    )
    fun defineReviewMode(
        @PathVariable systematicStudyId: UUID,
        @RequestBody request: PatchReviewModeRequest,
    ): ResponseEntity<*> {
        val presenter = RestfulDefineReviewModePresenter()
        val userId = authenticationInfoService.getAuthenticatedUserId()
        val requestModel = request.toRequestModel(userId, systematicStudyId)

        defineReviewModeService.define(presenter, requestModel)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
