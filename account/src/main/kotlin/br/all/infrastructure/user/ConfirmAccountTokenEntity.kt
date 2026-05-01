package br.all.infrastructure.user

import br.all.application.user.repository.TokenStatus
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "CONFIRM_ACCOUNT_TOKEN")
class ConfirmAccountTokenEntity (
    @Id
    @Column(nullable = false, unique = true)
    var token: UUID,

    var userId: UUID,

    @Enumerated(EnumType.STRING)
    var status: TokenStatus,

    var createdAt: LocalDateTime,
    var expiration: LocalDateTime,
)