package br.all.infrastructure.user

import br.all.application.user.repository.AccountCredentialsDto
import br.all.application.user.repository.UserAccountDto
import br.all.application.user.repository.UserAccountRepository
import br.all.application.user.repository.UserProfileDto
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

    override fun loadUserProfileById(id: UUID): UserProfileDto? =
        userAccountRepository.findById(id).orElse(null)?.toUserProfileDto()

    override fun loadFullUserAccountById(id: UUID): UserAccountDto? {
        val userAccount = userAccountRepository.findById(id).orElse(null) ?: return null
        val credentials = credentialsRepository.findById(id).orElse(null) ?: return null
        
        return UserAccountDto(
            id = userAccount.id,
            name = userAccount.name,
            username = credentials.username,
            password = credentials.password,
            email = userAccount.email,
            country = userAccount.country,
            affiliation = userAccount.affiliation,
            createdAt = userAccount.createdAt,
            authorities = credentials.authorities,
            refreshToken = credentials.refreshToken,
            isAccountNonExpired = credentials.isAccountNonExpired,
            isAccountNonLocked = credentials.isAccountNonLocked,
            isCredentialsNonExpired = credentials.isCredentialsNonExpired,
            isEnabled = credentials.isEnabled
        )
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