package br.all.infrastructure.user

import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

@Transactional
interface JpaAccountCredentialsRepository : JpaRepository<AccountCredentialsEntity, UUID>{
     fun findByUsername(username: String): AccountCredentialsEntity?
     fun findByRefreshToken(refreshToken: String): AccountCredentialsEntity?
     fun existsByUsername(username: String): Boolean

     @Modifying(clearAutomatically = true, flushAutomatically = true)
     @Query("update AccountCredentialsEntity e set e.refreshToken = :token where e.id = :id")
     fun updateRefreshTokenById(@Param(value = "id") id: UUID, @Param(value = "token") refreshToken: String?)
}