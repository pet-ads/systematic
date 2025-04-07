package br.all.infrastructure.collaboration

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document("invite")
data class InviteDocument(
    @Id val id: UUID,
    val systematicStudyId: UUID,
    val userId: UUID,
    val inviteDate: Date, 
    val expirationDate: Date, 
)
