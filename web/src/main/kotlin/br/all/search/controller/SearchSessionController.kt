package br.all.search.controller

import br.all.application.review.find.services.FindSystematicStudyService
import br.all.application.search.create.CreateSearchSessionService
import br.all.application.search.find.service.FindAllSearchSessionsBySourceService
import br.all.application.search.find.service.FindSearchSessionService
import br.all.application.search.find.service.FindAllSearchSessionsService
import br.all.application.search.update.UpdateSearchSessionService
import br.all.search.presenter.*
import br.all.security.service.AuthenticationInfoService
import br.all.utils.LinksFactory
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
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
@RequestMapping("api/v1/systematic-study/{systematicStudyId}")
class SearchSessionController(
    val createService: CreateSearchSessionService,
    val findOneService: FindSearchSessionService,
    val findAllService: FindAllSearchSessionsService,
    val findAllBySourceService: FindAllSearchSessionsBySourceService,
    val updateService: UpdateSearchSessionService,
    val mapper: ObjectMapper,
    val authenticationInfoService: AuthenticationInfoService,
    val linksFactory: LinksFactory
) {

    @Schema(name = "UpdateSearchSessionPutRequest")
    data class PutRequest(
        val searchString: String?,
        val additionalInfo: String?,
        val source: String?
    ) {
        fun toUpdateRequestModel(userId: UUID, systematicStudyId: UUID, sessionId: UUID) =
            UpdateSearchSessionService.RequestModel(
                userId, systematicStudyId, sessionId, searchString, additionalInfo, source
            )
    }

    @PostMapping("/search-session")
    @Operation(summary = "create a search session in the systematic study")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Success creating a search session in the systematic study",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = CreateSearchSessionService.ResponseModel::class)
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Fail creating a search session in the systematic study - invalid BibTeX format",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Fail creating a search session in the systematic study - unauthenticated user",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "403",
                description = "Fail creating a search session in the systematic study - unauthorized user",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Fail creating a search session in the systematic study - invalid request body",
                content = [Content(schema = Schema(hidden = true))]
            ),
        ]
    )
    fun createSearchSession(
        @PathVariable systematicStudyId: UUID,
        @RequestParam file: MultipartFile,
        @RequestParam data: String,
    ): ResponseEntity<*> {
        val presenter = RestfulCreateSearchSessionPresenter(linksFactory)
        val userId = authenticationInfoService.getAuthenticatedUserId()
        val jsonData: Map<String, Any> = mapper.readValue(data)
        val request = CreateRequest(
            userId = userId,
            systematicStudyId = systematicStudyId,
            source = jsonData["source"] as String,
            searchString = jsonData["searchString"] as String,
            additionalInfo = jsonData["additionalInfo"] as? String
        )
        createService.createSession(presenter, request, String(file.bytes))
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @GetMapping("/search-session")
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
            ApiResponse(
                responseCode = "401",
                description = "Fail finding search sessions in the systematic study - unauthenticated user",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "403",
                description = "Fail finding search sessions in the systematic study - unauthorized user",
                content = [Content(schema = Schema(hidden = true))]
            )
        ]
    )
    fun findAllSearchSessions(
        @PathVariable systematicStudyId: UUID,
    ): ResponseEntity<*> {
        val presenter = RestfulFindAllSearchSessionsPresenter(linksFactory)
        val userId = authenticationInfoService.getAuthenticatedUserId()
        val request = FindAllRequest(userId, systematicStudyId)
        findAllService.findAllSearchSessions(presenter, request)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @GetMapping("/search-session/{sessionId}")
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
                responseCode = "400",
                description = "Fail getting an existing search session in the systematic study - invalid id format",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Fail finding search sessions in the systematic study - unauthenticated user",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "403",
                description = "Fail finding search sessions in the systematic study - unauthorized user",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Fail getting an existing search session in the systematic study - not found",
                content = [Content(schema = Schema(hidden = true))]
            ),
        ]
    )
    fun findSearchSession(
        @PathVariable systematicStudyId: UUID,
        @PathVariable sessionId: UUID,
    ): ResponseEntity<*> {
        val presenter = RestfulFindSearchSessionPresenter(linksFactory)
        val userId = authenticationInfoService.getAuthenticatedUserId()
        val request = FindSearchSessionService.RequestModel(userId, systematicStudyId, sessionId)
        findOneService.findOneSession(presenter, request)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @GetMapping("/search-session-source/{source}")
    @Operation(summary = "Get all search sessions which have a given source of a systematic review")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success getting all search sessions in the systematic study. Either found all search sessions or none",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = FindAllSearchSessionsBySourceService.ResponseModel::class)
                )]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Fail finding search sessions in the systematic study - unauthenticated user",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "403",
                description = "Fail finding search sessions in the systematic study - unauthorized user",
                content = [Content(schema = Schema(hidden = true))]
            )
        ]
    )
    fun findSearchSessionsBySource(
        @PathVariable systematicStudyId: UUID,
        @PathVariable source: String
    ): ResponseEntity<*> {
        val presenter = RestfulFindAllSearchSessionsBySourcePresenter(linksFactory)
        val userId = authenticationInfoService.getAuthenticatedUserId()
        val request = FindAllSearchSessionsBySourceService.RequestModel(userId, systematicStudyId, source)
        findAllBySourceService.findAllSessionsBySource(presenter, request)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @Operation(summary = "Update an existing search session of a systematic study")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success updating an existing search session of a systematic study",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = UpdateSearchSessionService.ResponseModel::class)
                )]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Fail to update an existing search session - unauthenticated user",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "403",
                description = "Fail to update an existing search session - unauthorized user",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Fail to update an existing search session - study not found",
                content = [Content(schema = Schema(hidden = true))]
            ),
        ]
    )
    @PutMapping("/search-session/{sessionId}")
    fun updateSearchSession(
        @PathVariable systematicStudyId: UUID,
        @PathVariable sessionId: UUID,
        @RequestBody request: PutRequest
    ): ResponseEntity<*> {
        val presenter = RestfulUpdateSearchSessionPresenter(linksFactory)
        val userId = authenticationInfoService.getAuthenticatedUserId()
        val requestModel = request.toUpdateRequestModel(userId, systematicStudyId, sessionId)

        updateService.updateSession(presenter, requestModel)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }
//    fun patchSearchSession(
//        @PathVariable systematicStudyId: UUID,
//        @PathVariable sessionId: UUID,
//        @RequestParam filter: MultipartFile,
//        @RequestParam data: String
//    ): ResponseEntity<*> {
//        return
//    }
}