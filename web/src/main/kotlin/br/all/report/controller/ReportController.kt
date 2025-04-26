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
@RequestMapping("/api/v1/systematic-study/{systematicStudyId}/report/")
class ReportController(
    private val includedStudiesAnswersService: IncludedStudiesAnswersService,
    private val findCriteriaService: FindCriteriaService,
    private val findSourceService: FindSourceService,
    private val authorNetworkService: AuthorNetworkService,
    private val findKeywordsService: FindKeywordsService,
    private val findStudiesByStageService: FindStudiesByStageService,
    private val exportProtocolService: ExportProtocolService,
    private val findAnswerService: FindAnswerServiceImpl,
    private val studiesFunnelService: StudiesFunnelService,
    private val authenticationInfoService: AuthenticationInfoService,
    private val linksFactory: LinksFactory
) {

    @GetMapping("{studyReviewId}/included-studies-answers")
    @Operation(summary = "Retrieve all question answers")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success getting all answers of a question",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = RestfulIncludedStudiesAnswersPresenter::class)
                )]
            ),
            ApiResponse(responseCode = "401", description = "Failed getting all question answers - unauthenticated user",
                content = [Content(schema = Schema(hidden = true))]),
            ApiResponse(responseCode = "403", description = "Failed getting all question answers - unauthorized user",
                content = [Content(schema = Schema(hidden = true))]),
        ]
    )
    fun includedStudiesAnswers(
        @PathVariable systematicStudyId: UUID,
        @PathVariable studyReviewId: Long,
    ): ResponseEntity<*>{
        val presenter = RestfulIncludedStudiesAnswersPresenter(linksFactory)
        val userId = authenticationInfoService.getAuthenticatedUserId()
        val request = IncludedStudiesAnswersService.RequestModel(userId, systematicStudyId, studyReviewId)
        includedStudiesAnswersService.findAnswers(presenter, request)
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
        @PathVariable type: String,
    ): ResponseEntity<*> {
        val presenter = RestfulFindCriteriaPresenter(linksFactory)
        val userId = authenticationInfoService.getAuthenticatedUserId()
        val request = FindCriteriaService.RequestModel(userId, systematicStudyId, type)
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
                    schema = Schema(implementation = RestfulFindSourcePresenter::class)
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
        @PathVariable source: String,
    ): ResponseEntity<*> {
        val presenter = RestfulFindSourcePresenter(linksFactory)
        val userId = authenticationInfoService.getAuthenticatedUserId()
        val request = FindSourceService.RequestModel(userId, systematicStudyId, source)
        findSourceService.findSource(presenter, request)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @GetMapping("{studyReviewId}/author-network")
    @Operation(summary = "Get author-network graph")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success getting author-network graph",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = RestfulAuthorNetworkPresenter::class)
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

    @GetMapping("{studyReviewId}/keywords")
    @Operation(summary = "Count keywords")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success counting keywords",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = RestfulFindKeywordsPresenter::class)
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

    @GetMapping("studies/{stage}")
    @Operation(summary = "Get all studies given a stage")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success getting studies",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = RestfulFindStudiesByStagePresenter::class)
                )]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Failed getting studies - unauthenticated user",
                content = [Content(schema = Schema(hidden = true)
                )]),
            ApiResponse(
                responseCode = "403",
                description = "Failed getting studies - unauthenticated user",
                content = [Content(schema = Schema(hidden = true)
                )]),
        ]
    )
    fun findStudiesByStage(
        @PathVariable systematicStudyId: UUID,
        @PathVariable stage: String,
    ): ResponseEntity<*> {
        val presenter = RestfulFindStudiesByStagePresenter(linksFactory)
        val userId = authenticationInfoService.getAuthenticatedUserId()
        val request = FindStudiesByStageService.RequestModel(userId, systematicStudyId, stage)
        findStudiesByStageService.findStudiesByStage(presenter, request)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @GetMapping("studies-funnel")
    @Operation(summary = "Get studies-funnel")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success getting studies-funnel",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = RestfulStudiesFunnelPresenter::class)
                )]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Failed getting studies-funnel - unauthenticated user",
                content = [Content(schema = Schema(hidden = true)
                )]),
            ApiResponse(
                responseCode = "403",
                description = "Failed getting studies-funnel - unauthenticated user",
                content = [Content(schema = Schema(hidden = true)
                )]),
        ]
    )
    fun studiesFunnel(
        @PathVariable systematicStudyId: UUID,
    ): ResponseEntity<*> {
        val presenter = RestfulStudiesFunnelPresenter(linksFactory)
        val userId = authenticationInfoService.getAuthenticatedUserId()
        val request = StudiesFunnelService.RequestModel(userId, systematicStudyId)
        studiesFunnelService.studiesFunnel(presenter, request)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @GetMapping("exportable-protocol/{format}")
    @Operation(summary = "Export formatted protocol")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success exporting formatted protocol",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = RestfulExportProtocolPresenter::class)
                )]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Failed exporting formatted protocol - unauthenticated user",
                content = [Content(schema = Schema(hidden = true)
                )]),
            ApiResponse(
                responseCode = "403",
                description = "Failed exporting formatted protocol - unauthenticated user",
                content = [Content(schema = Schema(hidden = true)
                )]),
        ]
    )
    fun exportProtocol(
        @PathVariable systematicStudyId: UUID,
        @PathVariable format: String,
    ): ResponseEntity<*> {
        val presenter = RestfulExportProtocolPresenter(linksFactory)
        val userId = authenticationInfoService.getAuthenticatedUserId()
        val request = ExportProtocolService.RequestModel(userId, systematicStudyId, format.lowercase())
        exportProtocolService.exportProtocol(presenter, request)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @GetMapping("find-answer/{questionId}")
    @Operation(summary = "Find all answers given a question")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "All answers to thy question have been made manifest.",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = RestfulFindAnswerPresenter::class)
                )]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Failed finding all answers - unauthenticated user",
                content = [Content(schema = Schema(hidden = true)
                )]),
            ApiResponse(
                responseCode = "403",
                description = "Failed finding all answers - unauthenticated user",
                content = [Content(schema = Schema(hidden = true)
                )]),
        ]
    )
    fun findAnswer(
        @PathVariable systematicStudyId: UUID,
        @PathVariable questionId: UUID,
    ): ResponseEntity<*> {
        val presenter = RestfulFindAnswerPresenter(linksFactory)
        val userId = authenticationInfoService.getAuthenticatedUserId()
        val request = FindAnswerService.RequestModel(userId, systematicStudyId, questionId)
        findAnswerService.find(presenter, request)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }
}