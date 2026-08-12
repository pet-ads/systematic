package br.all.infrastructure.user

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface JpaUserAccountRepository : JpaRepository<UserAccountEntity, UUID> {
    fun existsByEmail(email: String): Boolean
    fun findByEmail(email: String): UserAccountEntity?

    @Query("""
    select u
    from UserAccountEntity u
    join u.accountCredentialsEntity c
    where (
        lower(u.email) like concat(:prefix, '%')
        or lower(c.username) like concat(:prefix, '%')
    )
    and c.isEnabled = true
""")
    fun findByUsernameOrEmailStartingWith(prefix: String, pageable: Pageable): List<UserAccountEntity>
}