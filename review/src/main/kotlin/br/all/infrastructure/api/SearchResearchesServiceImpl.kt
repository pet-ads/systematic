package br.all.infrastructure.api

import br.all.application.user.SearchResearchesService
import br.all.application.user.find.AccountSearchService
import br.all.application.user.repository.UserSummaryDto
import org.springframework.stereotype.Service

@Service
class SearchResearchesServiceImpl(private val accountSearchService : AccountSearchService): SearchResearchesService {
    override fun searchUsers(prefix: String): List<UserSummaryDto> {
        return accountSearchService.searchByPrefix(prefix)
    }
}