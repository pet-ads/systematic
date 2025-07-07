package br.all.collaboration.controller

import br.all.application.collaboration.create.AcceptInviteService
import br.all.application.collaboration.create.CreateInviteService
import br.all.collaboration.presenter.RestfulAcceptInvitePresenter
import br.all.collaboration.presenter.RestfulCreateInvitePresenter
import br.all.collaboration.requests.PostInviteRequest
import br.all.security.service.AuthenticationInfoService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/systematic-study/{systematicStudy}")
class CollaborationController(
    private val createInviteService: CreateInviteService,
    private val acceptInviteService: AcceptInviteService,
    private val authenticationInfoService: AuthenticationInfoService,
    ) {

    @PostMapping("/invite")
    @Operation(summary = "Invite an user in a systematic study")
    fun createInvite(
        @PathVariable systematicStudy: UUID,
        @RequestBody postRequest: PostInviteRequest
    ): ResponseEntity<*> {
        val presenter = RestfulCreateInvitePresenter()
        val userId = authenticationInfoService.getAuthenticatedUserId()
        val request = CreateInviteService.RequestModel(
            systematicStudy,
            userId,
            postRequest.inviteeId,
            postRequest.permissions.toSet()
        )
        createInviteService.createInvite(presenter, request)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @PostMapping("/invite/{inviteId}/accept")
    @Operation(summary = "Invite an user in a systematic study")
    fun acceptInvite(
        @PathVariable systematicStudy: UUID,
        @PathVariable inviteId: UUID
    ): ResponseEntity<*> {
        val presenter = RestfulAcceptInvitePresenter()
        val userId = authenticationInfoService.getAuthenticatedUserId()
        val request = AcceptInviteService.RequestModel(
            systematicStudy,
            userId,
            inviteId
        )
        acceptInviteService.acceptInvite(presenter, request)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }
}