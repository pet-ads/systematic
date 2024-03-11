package br.all.search.controller

import br.all.application.search.create.CreateSearchSessionService
import br.all.search.presenter.RestfulCreateSearchSessionPresenter
import com.fasterxml.jackson.databind.ObjectMapper
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*
import br.all.application.search.create.CreateSearchSessionService.RequestModel as CreateRequest

@RestController
@RequestMapping("api/v1/researcher/{researcherId}/systematic-study/{systematicStudyId}/search-session")
class SearchSessionController(
    val createService : CreateSearchSessionService,
    val mapper: ObjectMapper
) {

    @PostMapping
    @Operation(summary = "Create a search session")
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "Successful Operation"),
        ApiResponse(responseCode = "404", description = "Failed Operation for invalid request body"),
        ApiResponse(responseCode = "400", description = "Failed Operation for invalid BibTex format"),
        ApiResponse(responseCode = "403", description = "Failed Operation for not authorized researcher")
    ])
    fun createSearchSession(
        @PathVariable researcherId: UUID,
        @PathVariable systematicStudyId: UUID,
        @RequestParam file: MultipartFile,
        @RequestParam data: String,
    ) : ResponseEntity<*> {
        val presenter = RestfulCreateSearchSessionPresenter()
        val request = mapper.readValue(data, CreateRequest::class.java)
        createService.createSession(presenter, request, String(file.bytes))
        return presenter.responseEntity?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @GetMapping("/{sessionId}")
    @Operation(summary = "Find a search session using its Id")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Successful Operation"),
    ])
    fun findSearchSession(
        @PathVariable researcherId: UUID,
        @PathVariable systematicStudyId: UUID,
        @PathVariable sessionId: UUID,
    ): ResponseEntity<*> {
        return ResponseEntity<Void>(HttpStatus.NOT_IMPLEMENTED)
    }
}