package br.all.application.user.update

import java.util.UUID

interface ChangeAccountPasswordService {
    fun changePassword()

    data class RequestModel(
        val userId: UUID,
        val oldPassword: String,
        val newPassword: String,
        val confirmPassword: String
    )

    data class ResponseModel(
        val userId: UUID
    )
}