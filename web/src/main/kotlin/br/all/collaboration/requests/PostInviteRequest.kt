package br.all.collaboration.requests

import java.util.UUID

data class PostInviteRequest(
    val inviteeId: UUID,
    val permissions: List<String>
)
