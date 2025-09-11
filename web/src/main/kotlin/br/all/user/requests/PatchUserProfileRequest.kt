package br.all.user.requests

data class PatchUserProfileRequest(
    val name: String,
    val email: String,
    val affiliation: String,
    val country: String
)
