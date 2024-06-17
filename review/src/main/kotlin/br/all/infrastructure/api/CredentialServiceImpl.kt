package br.all.infrastructure.api

import br.all.application.user.CredentialsService
import br.all.application.user.CredentialsService.ResponseModel
import br.all.application.user.find.LoadAccountCredentialsService
import br.all.application.user.find.LoadAccountCredentialsService.UserSimpleCredentials
import org.springframework.stereotype.Service
import java.util.*

@Service
class CredentialServiceImpl(private val accountCredentialsService : LoadAccountCredentialsService): CredentialsService {
    override fun loadCredentials(userId: UUID): ResponseModel? {
        return accountCredentialsService.loadSimpleCredentialsById(userId).toResponseModel()
    }

    private fun UserSimpleCredentials.toResponseModel() = ResponseModel(this.id, this.username, this.authorities)
}