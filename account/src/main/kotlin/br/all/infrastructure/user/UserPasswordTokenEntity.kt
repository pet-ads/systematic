package br.all.infrastructure.user

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime

import java.util.UUID;

@Entity
@Table(name = "password_tokens")
class UserPasswordTokenEntity(

    @Id
    var id: UUID = UUID.randomUUID(),

    var userId: UUID,

    var email: String,

    var token: String,

    @Enumerated(EnumType.STRING)
    var status: TokenStatus,

    var createdAt: LocalDateTime,

    var expiration: LocalDateTime,

    var language: String
)