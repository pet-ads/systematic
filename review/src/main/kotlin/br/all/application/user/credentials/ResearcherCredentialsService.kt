package br.all.application.user.credentials

import br.all.domain.model.researcher.ResearcherId

interface ResearcherCredentialsService {
    fun isAuthenticated(researcherId: ResearcherId): Boolean
    fun hasAuthority(researcherId: ResearcherId): Boolean
}