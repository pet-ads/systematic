package br.all.application.researcher.repository

import java.util.UUID

interface ResearcherRepository {
    fun exists(id : UUID) : Boolean
}