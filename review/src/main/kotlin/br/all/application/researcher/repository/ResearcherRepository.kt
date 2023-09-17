package br.all.application.researcher.repository

import br.all.domain.model.researcher.ResearcherId

interface ResearcherRepository {
    fun existsById(id : ResearcherId) : Boolean
}