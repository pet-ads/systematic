package br.all.domain.user

import java.util.*


class UserAccount (
    private val uuid: UUID,
    private val username: String,
    private val password: String,
    private val email: String,
    private val country: String,
    private val affiliation: String,
)