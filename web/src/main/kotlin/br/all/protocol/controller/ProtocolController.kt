package br.all.protocol.controller

import br.all.application.protocol.find.FindProtocolService
import br.all.application.protocol.find.GetProtocolStageService
import br.all.application.protocol.update.UpdateProtocolService
import br.all.protocol.presenter.RestfulFindProtocolPresenter
import br.all.protocol.presenter.RestfulGetProtocolStagePresenter
import br.all.protocol.presenter.RestfulUpdateProtocolPresenter
import br.all.protocol.requests.PutRequest
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
import br.all.application.protocol.find.FindProtocolService.RequestModel as FindOneRequestModel
import br.all.application.protocol.find.GetProtocolStageService.RequestModel as FindStageRequestModel

@RestController
@RequestMapping("/systematic-study/{systematicStudyId}/protocol")
class ProtocolController(
    private val findProtocolService: FindProtocolService,
    private val updateProtocolService: UpdateProtocolService,
    private val getProtocolStageService: GetProtocolStageService,
    private val authenticationInfoService: AuthenticationInfoService,
    private val linksFactory: LinksFactory
) {

    @GetMapping
    @Operation(summary = "Get the protocol of a systematic study")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Success getting the protocol of a systematic study",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = FindProtocolService.ResponseModel::class)
            )]),
        ApiResponse(
            responseCode = "401",
            description = "Fail getting the protocol of a systematic study - unauthenticated collaborator",
            content = [Content(schema = Schema(hidden = true))]
        ),
        ApiResponse(
            responseCode = "403",
            description = "Fail getting the protocol of a systematic study - unauthorized collaborator",
            content = [Content(schema = Schema(hidden = true))]
        ),
        ApiResponse(
            responseCode = "404",
            description = "Fail getting the protocol of a systematic study - nonexistent protocol or systematic study",
            content = [Content(schema = Schema(hidden = true))]
        ),
    ])
    fun findById(
        @PathVariable systematicStudyId: UUID
    ): ResponseEntity<*> {
        val presenter = RestfulFindProtocolPresenter(linksFactory)
        val userId = authenticationInfoService.getAuthenticatedUserId()
        val request = FindOneRequestModel(userId, systematicStudyId)

        findProtocolService.findById(presenter, request)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @PutMapping
    @Operation(summary = "update the protocol of a systematic study")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Success updating the protocol of a systematic study",
            content = [Content(schema = Schema(hidden = true))]),
        ApiResponse(responseCode = "404", description = "Fail updating the protocol of a systematic study - nonexistent protocol or systematic study",
            content = [Content(schema = Schema(hidden = true))]),
        ApiResponse(responseCode = "403", description = "Fail updating the protocol of a systematic study - unauthorized collaborator",
            content = [Content(schema = Schema(hidden = true))]),
        ApiResponse(responseCode = "401", description = "Fail updating the protocol of a systematic study - unauthenticated collaborator",
            content = [Content(schema = Schema(hidden = true))])
    ])
    fun putProtocol(
        @PathVariable systematicStudyId: UUID,
        @RequestBody request: PutRequest,
    ): ResponseEntity<*> {
        val presenter = RestfulUpdateProtocolPresenter(linksFactory)
        val userId = authenticationInfoService.getAuthenticatedUserId()
        val requestModel = request.toUpdateRequestModel(userId, systematicStudyId)

        updateProtocolService.update(presenter, requestModel)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @GetMapping("/stage")
    @Operation(summary = "Get the current stage of the systematic review protocol")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Success getting the protocol stage",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = GetProtocolStageService.ResponseModel::class)
            )]),
        ApiResponse(
            responseCode = "401",
            description = "Fail getting the protocol stage - unauthenticated collaborator",
            content = [Content(schema = Schema(hidden = true))]
        ),
        ApiResponse(
            responseCode = "403",
            description = "Fail getting the protocol stage - unauthorized collaborator",
            content = [Content(schema = Schema(hidden = true))]
        ),
        ApiResponse(
            responseCode = "404",
            description = "Fail getting the protocol stage - nonexistent protocol or systematic study",
            content = [Content(schema = Schema(hidden = true))]
        ),
    ])
    fun getStage(
        @PathVariable systematicStudyId: UUID
    ) : ResponseEntity<*> {
        val presenter = RestfulGetProtocolStagePresenter(linksFactory)
        val userId = authenticationInfoService.getAuthenticatedUserId()
        val request = FindStageRequestModel(userId, systematicStudyId)

        getProtocolStageService.getStage(presenter, request)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
