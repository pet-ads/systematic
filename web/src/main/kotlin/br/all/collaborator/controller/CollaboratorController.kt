package br.all.collaborator.controller

import br.all.application.collaborator.find.FindAllCollaboratorsService
import br.all.application.collaborator.find.FindAllCollaboratorsService.FindAllCollaboratorsResponseModel
import br.all.application.collaborator.find.FindCollaboratorRoleService
import br.all.application.collaborator.find.FindCollaboratorRoleService.ResponseModel
import br.all.application.collaborator.find.SearchCollaboratorCandidatesService
import br.all.application.collaborator.find.SearchCollaboratorCandidatesService.RequestModel
import br.all.application.collaborator.invitation.InviteCollaboratorService
import br.all.application.collaborator.invitation.RespondInvitationService
import br.all.application.collaborator.leave.LeaveSystematicStudyService
import br.all.application.collaborator.remove.RemoveCollaboratorInviteService
import br.all.application.collaborator.remove.RemoveCollaboratorService
import br.all.application.collaborator.update.PassOwnershipService
import br.all.application.collaborator.update.UpdateResearcherRoleService
import br.all.collaborator.presenter.RestfulCreateInviteCollaboratorPresenter
import br.all.collaborator.presenter.RestfulFindAllCollaboratorsPresenter
import br.all.collaborator.presenter.RestfulFindCollaboratorRolePresenter
import br.all.collaborator.presenter.RestfulLeaveSystematicStudyPresenter
import br.all.collaborator.presenter.RestfulPassOwnershipPresenter
import br.all.collaborator.presenter.RestfulRemoveCollaboratorInvitePresenter
import br.all.collaborator.presenter.RestfulRemoveCollaboratorPresenter
import br.all.collaborator.presenter.RestfulRespondInvitationPresenter
import br.all.collaborator.presenter.RestfulSearchCollaboratorCandidatesPresenter
import br.all.collaborator.presenter.RestfulUpdateResearcherPresenter
import br.all.collaborator.request.InviteCollaboratorRequest
import br.all.collaborator.request.PassOwnershipRequest
import br.all.collaborator.request.RespondInvitationRequest
import br.all.collaborator.request.UpdateRoleRequest
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
    private val removeCollaboratorInviteService: RemoveCollaboratorInviteService,
    private val leaveSystematicStudyService: LeaveSystematicStudyService,
    private val passOwnershipService: PassOwnershipService,
    private val findAllCollaboratorsService: FindAllCollaboratorsService,
    private val findCollaboratorRoleService: FindCollaboratorRoleService,
    private val updateResearcherRoleService: UpdateResearcherRoleService,
    private val inviteCollaboratorService: InviteCollaboratorService,
    private val respondInvitationService: RespondInvitationService,
    private val searchCollaboratorCandidatesService: SearchCollaboratorCandidatesService,
) {
    @GetMapping("/{systematicStudyId}/collaborators")
    @Operation(summary = "Find all collaborators of a systematic study")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully found collaborators",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = FindAllCollaboratorsResponseModel::class)
                )]
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
                description = "Successfully found role",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ResponseModel::class)
                )]
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

    @DeleteMapping("/{systematicStudyId}/collaborator-invite/{researcherId}")
    @Operation(summary = "Remove an existing invite to a collaborator")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "204",
                description = "Successfully removed invite to a collaborator",
            ),
            ApiResponse(
                responseCode = "400",
                description = "Fail to remove invite - invalid systematic study",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Fail to remove invite - unauthenticated user",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "403",
                description = "Fail to remove invite - unauthorized user",
                content = [Content(schema = Schema(hidden = true))]
            )
        ]
    )
    fun removeCollaboratorInvite(
        @PathVariable systematicStudyId: UUID,
        @PathVariable researcherId: UUID,
    ): ResponseEntity<*> {
        val presenter = RestfulRemoveCollaboratorInvitePresenter()
        val userId = authenticationInfoService.getAuthenticatedUserId()
        val request = RemoveCollaboratorInviteService.RequestModel(
            userId,
            systematicStudyId,
            researcherId
        )

        removeCollaboratorInviteService.remove(presenter, request)

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

    @PostMapping("/{systematicStudyId}/invite-collaborator")
    @Operation(summary = "Create a invitation to a systematic study")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Success creating a invitation",
                content = [Content(schema = Schema(hidden = true))]),
            ApiResponse(
                responseCode = "400",
                description = "Fail creating a invitation - invalid data",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Fail creating a invitation - unauthenticated user",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "403",
                description = "Fail creating a invitation - User doesnt have enough permission to perform this action",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "403",
                description = "Fail creating a invitation - Collaborator user is not enabled",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Fail creating a invitation - Entity not found",
                content = [Content(schema = Schema(hidden = true))]
            ),
        ]
    )
    fun inviteCollaboratorToSystematicStudy(@PathVariable systematicStudyId: UUID, @RequestBody request: InviteCollaboratorRequest): ResponseEntity<*> {
        val presenter = RestfulCreateInviteCollaboratorPresenter()
        val userId = authenticationInfoService.getAuthenticatedUserId()
        val requestModel = request.toCreateRequestModel(systematicStudyId, userId)

        inviteCollaboratorService.create(presenter, requestModel)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @PostMapping("/respond-invitation")
    @Operation(summary = "Respond a invitation to collaborate on a systematic study")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Success responding the invitation",
                content = [Content(schema = Schema(hidden = true))]),
            ApiResponse(
                responseCode = "400",
                description = "Fail responding the invitation - invalid data",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Fail responding the invitation - Entity not found",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "409",
                description = "Fail responding the invitation - Data conflict",
                content = [Content(schema = Schema(hidden = true))]
            ),
        ]
    )
    fun respondInvitationService(@RequestBody request: RespondInvitationRequest): ResponseEntity<*> {
        val presenter = RestfulRespondInvitationPresenter()
        val requestModel = request.toCreateRequestModel(request.token, request.inviteResponse)

        respondInvitationService.respond(presenter, requestModel)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @GetMapping("/{systematicStudyId}/search-researchers")
    @Operation(summary = "Search researchers for a systematic study")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Success searching researchers",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = SearchCollaboratorCandidatesService.SearchResponseModel::class)
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Fail searching candidates - unauthenticated user",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Fail searching users with this prefix - not found",
                content = [Content(schema = Schema(hidden = true))]
            ),
        ]
    )
    fun searchCollaboratorCandidates(
        @PathVariable systematicStudyId: UUID,
        @RequestParam prefix: String
    ): ResponseEntity<*> {
        val presenter = RestfulSearchCollaboratorCandidatesPresenter()
        val requestModel = RequestModel(systematicStudyId, prefix)

        searchCollaboratorCandidatesService.findCandidatesWith(presenter, requestModel)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @PutMapping("/{systematicStudyId}/collaborator")
    @Operation(summary = "Update an existing collaborator role")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success updating an existing collaborator role",
                content = [Content(schema = Schema(hidden = true))]),
            ApiResponse(
                responseCode = "400",
                description = "Fail updating an existing collaborator role - invalid systematic study",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Fail updating an existing collaborator role - unauthenticated user",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "403",
                description = "Fail updating an existing collaborator role - unauthorized user",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(responseCode = "404", description = "Fail updating an existing collaborator role - not found",
                content = [Content(schema = Schema(hidden = true))]),
        ]
    )
    fun updateResearcherRole(@PathVariable systematicStudyId: UUID,
                             @RequestBody request: UpdateRoleRequest): ResponseEntity<*> {
        val presenter = RestfulUpdateResearcherPresenter()
        val userId = authenticationInfoService.getAuthenticatedUserId()
        val requestModel = request.toUpdateRequestModel(userId, systematicStudyId, request.researcherId, request.role)

        updateResearcherRoleService.update(presenter, requestModel)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }
}