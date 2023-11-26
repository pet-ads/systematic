package br.all.application.researcher.credentials

import br.all.domain.model.researcher.ResearcherId
import org.springframework.stereotype.Service

@Service
class FakeResearcherCredentialsService : ResearcherCredentialsService {
    override fun isAuthenticated(researcherId: ResearcherId) = true
    override fun hasAuthority(researcherId: ResearcherId) = true
}