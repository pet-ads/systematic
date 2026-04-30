package br.all.security.auth

import java.util.UUID

data class NewPasswordRequest(val token: UUID, val senha: String)

