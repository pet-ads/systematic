package br.all.user.requests

data class ChangeAccountPasswordRequest(
    val oldPassword: String,
    val newPassword: String,
    val confirmPassword: String
)