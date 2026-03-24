package br.all.application.user.repository

import UserPasswordTokenEntity

fun UserPasswordTokenEntity.toDto() = UserPasswordTokenDto(
    id = id,
    userId = userId,
    email = email,
    token = token,
    status = status,
    createdAt = createdAt,
    expiration = expiration,
    language = language
)

fun UserPasswordTokenDto.toEntity() = UserPasswordTokenEntity(
    id = id,
    userId = userId,
    email = email,
    token = token,
    status = status,
    createdAt = createdAt,
    expiration = expiration,
    language = language
)