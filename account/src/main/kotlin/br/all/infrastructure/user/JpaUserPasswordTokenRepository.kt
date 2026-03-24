package br.all.infrastructure.user

import UserPasswordTokenEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface JpaUserPasswordTokenRepository :
    JpaRepository<UserPasswordTokenEntity, UUID> {

    fun findByEmailAndStatus(email: String, status: TokenStatus): UserPasswordTokenEntity?

    fun findByToken(token: String): UserPasswordTokenEntity?

}