package br.all.security.auth

data class AuthenticationResponse (
    val accessToken: String,
    val refreshToken: String,
)
