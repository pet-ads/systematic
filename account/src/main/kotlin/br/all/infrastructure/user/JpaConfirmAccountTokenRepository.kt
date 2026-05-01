package br.all.infrastructure.user

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface JpaConfirmAccountTokenRepository : JpaRepository<ConfirmAccountTokenEntity, UUID> {
    fun findByUserId(userId: UUID): ConfirmAccountTokenEntity?
}