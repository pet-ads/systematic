package br.all.infrastructure.user

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface JpaUserAccountRepository : JpaRepository<UserAccountEntity, UUID> {
    fun existsByEmail(email: String): Boolean
}