package br.all.infrastructure.user

import br.all.application.user.repository.ConfirmAccountTokenDto

fun ConfirmAccountTokenEntity.toDto() = ConfirmAccountTokenDto(
    token = token,
    userId = userId,
    status = status,
    createdAt = createdAt,
    expiration = expiration,
)

fun ConfirmAccountTokenDto.toEntity() = ConfirmAccountTokenEntity(
    token = token,
    userId = userId,
    status = status,
    createdAt = createdAt,
    expiration = expiration,
)