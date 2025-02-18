package br.all.application.user.update

import java.util.UUID

interface UpdateRefreshTokenService {
    fun update(request: RequestModel)

    data class RequestModel(
        val userId: UUID,
        val refreshToken: String?
    )
}