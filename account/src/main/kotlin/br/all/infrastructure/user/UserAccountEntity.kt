package br.all.infrastructure.user

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "USER_ACCOUNTS")
class UserAccountEntity (
    @Id var id: UUID,
    @Column(nullable = false)
    var name: String,
    @OneToOne( mappedBy = "userAccount", cascade = [CascadeType.ALL])
    @PrimaryKeyJoinColumn
    var accountCredentialsEntity: AccountCredentialsEntity,
    @Column(unique=true, nullable = false)
    var email: String,
    var country: String,
    var affiliation: String,
    var createdAt: LocalDateTime
)