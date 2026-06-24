package br.all.application.user.find

import br.all.application.user.repository.UserAccountRepository
import br.all.application.user.repository.UserSummaryDto
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class AccountSearchServiceImpl(private val repository: UserAccountRepository): AccountSearchService {
    companion object {
        private const val SEARCH_PAGE_SIZE = 20
    }

    override fun searchByPrefix(prefix: String): List<UserSummaryDto> {
        val pageable = PageRequest.of(0, SEARCH_PAGE_SIZE)
        return repository.findByUsernameOrEmailStartingWith(prefix, pageable)
    }
}