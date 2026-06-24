package br.all.infrastructure.api

import br.all.application.user.SearchResearchesService
import br.all.application.user.SearchResearchesService.ResponseModel
import br.all.application.user.find.AccountSearchService
import br.all.application.user.repository.UserSummaryDto
import org.springframework.stereotype.Service

@Service
class SearchResearchesServiceImpl(private val accountSearchService : AccountSearchService): SearchResearchesService {
    override fun searchUsers(prefix: String): List<ResponseModel> {
        return accountSearchService.searchByPrefix(prefix).map { it.toResponseModel() }
    }

    private fun UserSummaryDto.toResponseModel() = ResponseModel(this.id, this.username, this.email)
}