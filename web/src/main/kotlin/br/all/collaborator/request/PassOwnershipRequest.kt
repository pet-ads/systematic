package br.all.collaborator.request

import java.util.UUID

data class PassOwnershipRequest(
    val newOwnerId: UUID,
)