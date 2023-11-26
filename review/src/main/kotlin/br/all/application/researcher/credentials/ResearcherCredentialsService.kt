package br.all.application.researcher.credentials

import br.all.domain.model.researcher.ResearcherId
import org.springframework.stereotype.Service

interface ResearcherCredentialsService {
    fun isAuthenticated(researcherId: ResearcherId): Boolean
    fun hasAuthority(researcherId: ResearcherId): Boolean

}