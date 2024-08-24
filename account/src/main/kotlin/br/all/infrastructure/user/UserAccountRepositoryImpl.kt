package br.all.infrastructure.user

import br.all.application.user.repository.AccountCredentialsDto
import br.all.application.user.repository.UserAccountDto
import br.all.application.user.repository.UserAccountRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class UserAccountRepositoryImpl(
    private val userAccountRepository: JpaUserAccountRepository,
    private val credentialsRepository: JpaAccountCredentialsRepository
) : UserAccountRepository {

    override fun save(dto: UserAccountDto) {
        val entity = dto.toUserAccountEntity()
        userAccountRepository.save(entity)
    }

    override fun deleteById(id: UUID) {
        userAccountRepository.deleteById(id)
        credentialsRepository.deleteById(id)
    }

    override fun loadCredentialsByUsername(username: String) =
        credentialsRepository.findByUsername(username)?.toAccountCredentialsDto()

    override fun loadCredentialsByToken(token: String): AccountCredentialsDto? =
        credentialsRepository.findByRefreshToken(token)?.toAccountCredentialsDto()

    override fun loadCredentialsById(id: UUID): AccountCredentialsDto? =
        credentialsRepository.findById(id).orElse(null)?.toAccountCredentialsDto()

    override fun updateRefreshToken(userId: UUID, token: String?) {
        credentialsRepository.updateRefreshTokenById(userId, token)
    }

    override fun existsByEmail(email: String) = userAccountRepository.existsByEmail(email)

    override fun existsByUsername(username: String) = credentialsRepository.existsByUsername(username)
}