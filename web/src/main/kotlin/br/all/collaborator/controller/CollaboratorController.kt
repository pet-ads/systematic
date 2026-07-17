package br.all.collaborator.controller

import br.all.application.collaborator.leave.LeaveSystematicStudyService
import br.all.application.collaborator.remove.RemoveCollaboratorService
import br.all.collaborator.presenter.RestfulLeaveSystematicStudyPresenter
import br.all.collaborator.presenter.RestfulRemoveCollaboratorPresenter
import br.all.security.service.AuthenticationInfoService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/systematic-study")
class CollaboratorController(
    private val authenticationInfoService: AuthenticationInfoService,
    private val removeCollaboratorService: RemoveCollaboratorService,
    private val leaveSystematicStudyService: LeaveSystematicStudyService,
) {

    @DeleteMapping("/{systematicStudyId}/collaborator/{researcherId}")
    @Operation(summary = "Remove an existing collaborator of a systematic study")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "204",
                description = "Successfully removed collaborator"
            ),
            ApiResponse(
                responseCode = "400",
                description = "Fail to remove collaborator - invalid systematic study",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Fail to remove collaborator - unauthenticated user",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "403",
                description = "Fail to remove collaborator - unauthorized user",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Fail to remove collaborator - collaborator not found",
                content = [Content(schema = Schema(hidden = true))]
            )
        ]
    )
    fun removeCollaborator(
        @PathVariable systematicStudyId: UUID,
        @PathVariable researcherId: UUID,
    ): ResponseEntity<*> {
        val presenter = RestfulRemoveCollaboratorPresenter()
        val userId = authenticationInfoService.getAuthenticatedUserId()
        val request = RemoveCollaboratorService.RequestModel(
            userId = userId,
            systematicStudyId = systematicStudyId,
            researcherId = researcherId
        )

        removeCollaboratorService.remove(presenter, request)

        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }


    @DeleteMapping("/{systematicStudyId}/collaborator/me")
    @Operation(summary = "Leave from an existing systematic study")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "204",
                description = "Successfully leaving from systematic study"
            ),
            ApiResponse(
                responseCode = "400",
                description = "Fail to leave from systematic study - invalid systematic study",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Fail to leave from systematic study - unauthenticated user",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "403",
                description = "Fail to leave from systematic study - unauthorized user",
                content = [Content(schema = Schema(hidden = true))]
            )
        ]
    )
    fun leaveSystematicStudy(
        @PathVariable systematicStudyId: UUID
    ): ResponseEntity<*> {
        val presenter = RestfulLeaveSystematicStudyPresenter()
        val userId = authenticationInfoService.getAuthenticatedUserId()
        val request = LeaveSystematicStudyService.RequestModel(
            userId = userId,
            systematicStudyId = systematicStudyId
        )

        leaveSystematicStudyService.leave(presenter, request)

        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }
}