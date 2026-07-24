package br.all.collaborator.controller

import br.all.application.collaborator.find.FindAllCollaboratorsService
import br.all.application.collaborator.find.FindCollaboratorRoleService
import br.all.application.collaborator.leave.LeaveSystematicStudyService
import br.all.application.collaborator.remove.RemoveCollaboratorService
import br.all.application.collaborator.update.PassOwnershipService
import br.all.collaborator.presenter.RestfulFindAllCollaboratorsPresenter
import br.all.collaborator.presenter.RestfulFindCollaboratorRolePresenter
import br.all.collaborator.presenter.RestfulLeaveSystematicStudyPresenter
import br.all.collaborator.presenter.RestfulPassOwnershipPresenter
import br.all.collaborator.presenter.RestfulRemoveCollaboratorPresenter
import br.all.collaborator.request.PassOwnershipRequest
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
    private val passOwnershipService: PassOwnershipService,
    private val findAllCollaboratorsService: FindAllCollaboratorsService,
    private val findCollaboratorRoleService: FindCollaboratorRoleService,
) {
    @GetMapping("/{systematicStudyId}/collaborators")
    @Operation(summary = "Find all collaborators of a systematic study")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully found collaborators"
            ),
            ApiResponse(
                responseCode = "400",
                description = "Fail to find collaborators - invalid systematic study",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Fail to find collaborators - unauthenticated user",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "403",
                description = "Fail to find collaborators - unauthorized user",
                content = [Content(schema = Schema(hidden = true))]
            )
        ]
    )
    fun findAllCollaborators(
        @PathVariable systematicStudyId: UUID
    ): ResponseEntity<*> {
        val presenter = RestfulFindAllCollaboratorsPresenter()
        val userId = authenticationInfoService.getAuthenticatedUserId()
        val request = FindAllCollaboratorsService.Request(
            userId = userId,
            systematicStudyId = systematicStudyId
        )
        findAllCollaboratorsService.findAll(presenter, request)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @GetMapping("/{systematicStudyId}/collaborator/me/role")
    @Operation(summary = "Find the authenticated user's role in a systematic study")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully found role"
            ),
            ApiResponse(
                responseCode = "400",
                description = "Fail to find role - invalid systematic study",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Fail to find role - unauthenticated user",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "403",
                description = "Fail to find role - unauthorized user",
                content = [Content(schema = Schema(hidden = true))]
            )
        ]
    )
    fun findCollaboratorRole(
        @PathVariable systematicStudyId: UUID
    ): ResponseEntity<*> {
        val presenter = RestfulFindCollaboratorRolePresenter()
        val userId = authenticationInfoService.getAuthenticatedUserId()
        val request = FindCollaboratorRoleService.Request(
            userId = userId,
            systematicStudyId = systematicStudyId
        )
        findCollaboratorRoleService.findRole(presenter, request)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

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

    @PutMapping("/{systematicStudyId}/owner")
    @Operation(summary = "Pass ownership of a systematic study to an existing collaborator")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "204",
                description = "Successfully passed ownership"
            ),
            ApiResponse(
                responseCode = "400",
                description = "Fail to pass ownership - invalid systematic study or already owner",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Fail to pass ownership - unauthenticated user",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "403",
                description = "Fail to pass ownership - unauthorized user",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Fail to pass ownership - collaborator not found",
                content = [Content(schema = Schema(hidden = true))]
            )
        ]
    )
    fun passOwnership(
        @PathVariable systematicStudyId: UUID,
        @RequestBody request: PassOwnershipRequest
    ): ResponseEntity<*> {
        val presenter = RestfulPassOwnershipPresenter()
        val userId = authenticationInfoService.getAuthenticatedUserId()
        val request = PassOwnershipService.RequestModel(
            userId = userId,
            systematicStudyId = systematicStudyId,
            newOwnerId = request.newOwnerId
        )

        passOwnershipService.pass(presenter, request)

        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }
}