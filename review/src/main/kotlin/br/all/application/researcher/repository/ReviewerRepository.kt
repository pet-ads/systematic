package br.all.application.researcher.repository

import java.util.UUID

interface ReviewerRepository {
    fun existsById(id : UUID) : Boolean
}