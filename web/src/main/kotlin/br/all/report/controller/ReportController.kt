package br.all.report.controller

import br.all.application.report.find.service.FindAnswersService
import br.all.report.presenter.RestfulFindAnswersPresenter
import br.all.security.service.AuthenticationInfoService
import br.all.utils.LinksFactory
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/v1/systematic-study/{systematicStudy}/report")
class ReportController(
    private val findAnswersService: FindAnswersService,
    private val authenticationInfoService: AuthenticationInfoService,
    private val linksFactory: LinksFactory
) {

    @GetMapping("question/{studyReviewId}/{questionId}")
    @Operation(summary = "Retrieve all question answers")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success getting all answers of a question",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = RestfulFindAnswersPresenter::class)
                )]
            ),
            ApiResponse(responseCode = "401", description = "Fail getting all question answers - unauthenticated user",
                content = [Content(schema = Schema(hidden = true))]),
            ApiResponse(responseCode = "403", description = "Fail getting all question answers - unauthorized user",
                content = [Content(schema = Schema(hidden = true))]),
        ]
    )
    fun findAnswers(
        @PathVariable systematicStudy: UUID,
        @PathVariable studyReviewId: Long,
        @PathVariable questionId: UUID,
    ): ResponseEntity<*>{
        val presenter = RestfulFindAnswersPresenter(linksFactory)
        val userId = authenticationInfoService.getAuthenticatedUserId()
        val request = FindAnswersService.RequestModel(userId, systematicStudy, studyReviewId, questionId)
        findAnswersService.findAnswers(presenter, request)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

}