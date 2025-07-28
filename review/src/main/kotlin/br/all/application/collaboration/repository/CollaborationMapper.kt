package br.all.application.collaboration.repository

import br.all.domain.model.collaboration.*
import br.all.domain.model.review.toSystematicStudyId
import br.all.domain.model.user.toResearcherId

fun Invite.toDto(): InviteDto = InviteDto(
    id = this.id.value(),
    systematicStudyId = this.systematicStudyId.value(),
    userId = this.userId.value,
    inviteDate = this.inviteDate,
    expirationDate = this.expirationDate,
    permissions = this.permissions.map { it.name }.toSet()
)

fun InviteDto.toDomain(): Invite = Invite(
    id = this.id.toInviteId(),
    systematicStudyId = this.systematicStudyId.toSystematicStudyId(),
    userId = this.userId.toResearcherId(),
    permissions = this.permissions.map { CollaborationPermission.valueOf(it) }.toSet(),
    inviteDate = this.inviteDate,
    expirationDate = this.expirationDate
)

fun Collaboration.toDto(): CollaborationDto = CollaborationDto(
    id = this.id.value(),
    systematicStudyId = this.systematicStudyId.value(),
    userId = this.userId.value,
    status = this.status.name,
    permissions = this.permissions.map { it.name }.toSet()
)

fun CollaborationDto.toDomain(): Collaboration = Collaboration(
    id = this.id.toCollaborationId(),
    systematicStudyId = this.systematicStudyId.toSystematicStudyId(),
    userId = this.userId.toResearcherId(),
    status = CollaborationStatus.valueOf(this.status),
    permissions = this.permissions.map { CollaborationPermission.valueOf(it) }.toSet()
)