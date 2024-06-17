package br.all.security.service

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User
import java.util.*

class ApplicationUser(
    val id: UUID,
    username: String,
    password: String,
    authorities: Set<GrantedAuthority>? = emptySet(),
    accountNonExpired: Boolean = true,
    accountNonLocked: Boolean = true,
    credentialsNonExpired: Boolean = true,
    enabled: Boolean = true,
) : User(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities)