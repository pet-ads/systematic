package br.all.application.user.credentials

import br.all.domain.shared.user.ResearcherId
import org.springframework.stereotype.Service

@Service
class FakeResearcherCredentialsService : ResearcherCredentialsService {
    override fun isAuthenticated(researcherId: ResearcherId) = true
    override fun hasAuthority(researcherId: ResearcherId) = true
}