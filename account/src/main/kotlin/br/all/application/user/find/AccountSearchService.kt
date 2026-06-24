package br.all.application.user.find

import br.all.application.user.repository.UserSummaryDto

interface AccountSearchService {
    fun searchByPrefix(prefix: String): List<UserSummaryDto>
}