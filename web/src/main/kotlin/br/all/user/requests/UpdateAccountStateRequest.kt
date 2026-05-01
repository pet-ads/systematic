package br.all.user.requests

import java.util.UUID

data class UpdateAccountStateRequest(
    val token: UUID
)
