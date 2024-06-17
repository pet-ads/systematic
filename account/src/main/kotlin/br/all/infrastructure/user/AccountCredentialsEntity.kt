package br.all.infrastructure.user

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "USER_CREDENTIALS")
class AccountCredentialsEntity(
    @Id
    var id: UUID,
    @Column(unique = true, nullable = false)
    var username: String,
    @Column(nullable = false)
    var password: String,

    @ElementCollection
    @Column(name = "authority")
    val authorities: Set<String>,

    @Column(unique = true)
    val refreshToken: String?,

    var isAccountNonExpired : Boolean,
    var isAccountNonLocked : Boolean,
    var isCredentialsNonExpired : Boolean,
    var isEnabled : Boolean
){
    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    lateinit var userAccount: UserAccountEntity
}

