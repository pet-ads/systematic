package br.all.infrastructure.user

import br.all.application.user.repository.UserPasswordTokenDto

fun UserPasswordTokenEntity.toDto() = UserPasswordTokenDto(
    token = token,
    userId = userId,
    email = email,
    status = status,
    createdAt = createdAt,
    expiration = expiration,
    language = language
)

fun UserPasswordTokenDto.toEntity() = UserPasswordTokenEntity(
    token = token,
    userId = userId,
    email = email,
    status = status,
    createdAt = createdAt,
    expiration = expiration,
    language = language
)