package br.all.report.controller

import br.all.application.report.find.service.*
import br.all.report.presenter.*
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
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/v1/systematic-study/{systematicStudyId}/report/{studyReviewId}/")
class ReportController(
    private val findAnswersService: FindAnswersService,
    private val findCriteriaService: FindCriteriaService,
    private val findSourceService: FindSourceService,
    private val authorNetworkService: AuthorNetworkService,
    private val findKeywordsService: FindKeywordsService,
    private val authenticationInfoService: AuthenticationInfoService,
    private val linksFactory: LinksFactory
) {

    @GetMapping("question/{questionId}")
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
            ApiResponse(responseCode = "401", description = "Failed getting all question answers - unauthenticated user",
                content = [Content(schema = Schema(hidden = true))]),
            ApiResponse(responseCode = "403", description = "Failed getting all question answers - unauthorized user",
                content = [Content(schema = Schema(hidden = true))]),
        ]
    )
    fun findAnswers(
        @PathVariable systematicStudyId: UUID,
        @PathVariable studyReviewId: Long,
        @PathVariable questionId: UUID,
    ): ResponseEntity<*>{
        val presenter = RestfulFindAnswersPresenter(linksFactory)
        val userId = authenticationInfoService.getAuthenticatedUserId()
        val request = FindAnswersService.RequestModel(userId, systematicStudyId, studyReviewId, questionId)
        findAnswersService.findAnswers(presenter, request)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @GetMapping("criteria/{type}")
    @Operation(summary = "Get all studies included or excluded by criteria")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success getting all studies included or excluded by criteria",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = RestfulFindCriteriaPresenter::class)
                )]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Failed to get all studies included or excluded by criteria - unauthenticated user",
                content = [Content(schema = Schema(hidden = true)
                )]),
            ApiResponse(
                responseCode = "403",
                description = "Failed getting all studies included or excluded by criteria - unauthorized user",
                content = [Content(schema = Schema(hidden = true)
                )]),
        ]
    )
    fun findCriteria(
        @PathVariable systematicStudyId: UUID,
        @PathVariable studyReviewId: Long,
        @PathVariable type: String,
    ): ResponseEntity<*> {
        val presenter = RestfulFindCriteriaPresenter(linksFactory)
        val userId = authenticationInfoService.getAuthenticatedUserId()
        val request = FindCriteriaService.RequestModel(userId, systematicStudyId, studyReviewId, type)
        findCriteriaService.findCriteria(presenter, request)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @GetMapping("source/{source}")
    @Operation(summary = "Get all studies of source")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success getting all studies included, excluded or duplicated by source",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = RestfulFindCriteriaPresenter::class)
                )]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Failed getting all studies included, excluded or duplicated by source - unauthenticated user",
                content = [Content(schema = Schema(hidden = true)
                )]),
            ApiResponse(
                responseCode = "403",
                description = "Failed getting all studies included, excluded or duplicated by source - unauthenticated user",
                content = [Content(schema = Schema(hidden = true)
                )]),
        ]
    )
    fun findSource(
        @PathVariable systematicStudyId: UUID,
        @PathVariable studyReviewId: Long,
        @PathVariable source: String,
    ): ResponseEntity<*> {
        val presenter = RestfulFindSourcePresenter(linksFactory)
        val userId = authenticationInfoService.getAuthenticatedUserId()
        val request = FindSourceService.RequestModel(userId, systematicStudyId, studyReviewId, source)
        findSourceService.findSource(presenter, request)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @GetMapping("author-network")
    @Operation(summary = "Get author-network graph")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success getting author-network graph",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = RestfulFindCriteriaPresenter::class)
                )]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Failed getting author-network graph - unauthenticated user",
                content = [Content(schema = Schema(hidden = true)
                )]),
            ApiResponse(
                responseCode = "403",
                description = "Failed getting author-network graph - unauthenticated user",
                content = [Content(schema = Schema(hidden = true)
                )]),
        ]
    )
    fun authorNetwork(
        @PathVariable systematicStudyId: UUID,
        @PathVariable studyReviewId: Long,
    ): ResponseEntity<*> {
        val presenter = RestfulAuthorNetworkPresenter(linksFactory)
        val userId = authenticationInfoService.getAuthenticatedUserId()
        val request = AuthorNetworkService.RequestModel(userId, systematicStudyId, studyReviewId)
        authorNetworkService.findAuthors(presenter, request)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @GetMapping("keywords")
    @Operation(summary = "Count keywords")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success counting keywords",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = RestfulFindCriteriaPresenter::class)
                )]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Failed counting keywords - unauthenticated user",
                content = [Content(schema = Schema(hidden = true)
                )]),
            ApiResponse(
                responseCode = "403",
                description = "Failed counting keywords - unauthenticated user",
                content = [Content(schema = Schema(hidden = true)
                )]),
        ]
    )
    fun findKeywords(
        @PathVariable systematicStudyId: UUID,
        @PathVariable studyReviewId: Long,
        @RequestParam("filter") filter: String?,
    ): ResponseEntity<*> {
        val presenter = RestfulFindKeywordsPresenter(linksFactory)
        val userId = authenticationInfoService.getAuthenticatedUserId()
        val request = FindKeywordsService.RequestModel(userId, systematicStudyId, studyReviewId, filter)
        findKeywordsService.findKeywords(presenter, request)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }
}