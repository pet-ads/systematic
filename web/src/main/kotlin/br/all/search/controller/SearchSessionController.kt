package br.all.search.controller

import br.all.application.search.create.CreateSearchSessionService
import br.all.application.search.find.service.FindSearchSessionService
import br.all.search.presenter.RestfulCreateSearchSessionPresenter
import br.all.search.presenter.RestfulFindSearchSessionPresenter
import br.all.application.search.find.service.FindAllSearchSessionsService
import br.all.search.presenter.RestfulFindAllSearchSessionsPresenter
import com.fasterxml.jackson.databind.ObjectMapper
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*
import br.all.application.search.create.CreateSearchSessionService.RequestModel as CreateRequest
import br.all.application.search.find.service.FindAllSearchSessionsService.RequestModel as FindAllRequest

@RestController
@RequestMapping("api/v1/researcher/{researcherId}/systematic-study/{systematicStudyId}/search-session")
class SearchSessionController(
    val createService: CreateSearchSessionService,
    val findOneService: FindSearchSessionService,
    val findAllService: FindAllSearchSessionsService,
    val mapper: ObjectMapper
) {

    @PostMapping
    @Operation(summary = "create a search session in the systematic study")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Success creating a search session in the systematic study"
            ),
            ApiResponse(
                responseCode = "400",
                description = "Fail creating a search session in the systematic study - invalid BibTeX format"
            ),
            ApiResponse(
                responseCode = "404",
                description = "Fail creating a search session in the systematic study - invalid request body"
            ),
            ApiResponse(
                responseCode = "403",
                description = "Fail creating a search session in the systematic study - unauthorized researcher"
            )
        ]
    )
    fun createSearchSession(
        @PathVariable researcherId: UUID,
        @PathVariable systematicStudyId: UUID,
        @RequestParam file: MultipartFile,
        @RequestParam data: String,
    ): ResponseEntity<*> {
        val presenter = RestfulCreateSearchSessionPresenter()
        val request = mapper.readValue(data, CreateRequest::class.java)
        createService.createSession(presenter, request, String(file.bytes))
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @GetMapping
    @Operation(summary = "Get all search sessions of a systematic review")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success getting all search sessions in the systematic study. Either found all search sessions or none",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = FindAllSearchSessionsService.ResponseModel::class)
                )]
            ),
        ]
    )
    fun findAllSearchSessions(
        @PathVariable researcherId: UUID,
        @PathVariable systematicStudyId: UUID,
    ): ResponseEntity<*> {
        val presenter = RestfulFindAllSearchSessionsPresenter()
        val request = FindAllRequest(researcherId, systematicStudyId)
        findAllService.findAllSearchSessions(presenter, request)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @GetMapping("/{sessionId}")
    @Operation(summary = "Get an existing search session of a systematic review")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success getting an existing search session in the systematic study",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = FindSearchSessionService.ResponseModel::class)
                )]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Fail getting an existing search session in the systematic study - not found"
            ),
            ApiResponse(
                responseCode = "400",
                description = "Fail getting an existing search session in the systematic study - invalid id format"
            ),
        ]
    )
    fun findSearchSession(
        @PathVariable researcherId: UUID,
        @PathVariable systematicStudyId: UUID,
        @PathVariable sessionId: UUID,
    ): ResponseEntity<*> {
        val presenter = RestfulFindSearchSessionPresenter()
        val request = FindSearchSessionService.RequestModel(researcherId, systematicStudyId, sessionId)
        findOneService.findOneSession(presenter, request)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }
}