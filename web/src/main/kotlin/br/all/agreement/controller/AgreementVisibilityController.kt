package br.all.agreement.controller

import br.all.agreement.presenter.RestfulGetAgreementVisibilityPresenter
import br.all.agreement.presenter.RestfulSetAgreementVisibilityPresenter
import br.all.agreement.requests.PutAgreementVisibilityRequest
import br.all.application.agreement.visibility.GetAgreementVisibilityService
import br.all.application.agreement.visibility.SetAgreementVisibilityService
import br.all.security.service.AuthenticationInfoService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/systematic-study/{systematicStudy}/agreement/visibility")
class AgreementVisibilityController(
    private val setAgreementVisibilityService: SetAgreementVisibilityService,
    private val getAgreementVisibilityService: GetAgreementVisibilityService,
    private val authenticationInfoService: AuthenticationInfoService,
) {

    @GetMapping
    @Operation(summary = "Get the agreement visibility of each stage of a systematic study")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success getting the agreement visibility",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = GetAgreementVisibilityService.ResponseModel::class),
                )],
            ),
            ApiResponse(responseCode = "401", description = "Unauthenticated user",
                content = [Content(schema = Schema(hidden = true))]),
            ApiResponse(responseCode = "403", description = "Unauthorized user",
                content = [Content(schema = Schema(hidden = true))]),
            ApiResponse(responseCode = "404", description = "Systematic study not found",
                content = [Content(schema = Schema(hidden = true))]),
        ],
    )
    fun getVisibility(@PathVariable systematicStudy: UUID): ResponseEntity<*> {
        val presenter = RestfulGetAgreementVisibilityPresenter()
        val userId = authenticationInfoService.getAuthenticatedUserId()
        getAgreementVisibilityService.getVisibility(
            presenter, GetAgreementVisibilityService.RequestModel(userId, systematicStudy)
        )
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @PutMapping
    @Operation(summary = "Limit or release the agreement visibility of a stage (owner or admin only)")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success changing the agreement visibility",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = SetAgreementVisibilityService.ResponseModel::class),
                )],
            ),
            ApiResponse(responseCode = "400", description = "Invalid stage or visibility",
                content = [Content(schema = Schema(hidden = true))]),
            ApiResponse(responseCode = "401", description = "Unauthenticated user",
                content = [Content(schema = Schema(hidden = true))]),
            ApiResponse(responseCode = "403", description = "User is not the owner nor an admin",
                content = [Content(schema = Schema(hidden = true))]),
            ApiResponse(responseCode = "404", description = "Systematic study not found",
                content = [Content(schema = Schema(hidden = true))]),
        ],
    )
    fun setVisibility(
        @PathVariable systematicStudy: UUID,
        @RequestBody request: PutAgreementVisibilityRequest,
    ): ResponseEntity<*> {
        val presenter = RestfulSetAgreementVisibilityPresenter()
        val userId = authenticationInfoService.getAuthenticatedUserId()
        setAgreementVisibilityService.setVisibility(presenter, request.toRequestModel(userId, systematicStudy))
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }
}