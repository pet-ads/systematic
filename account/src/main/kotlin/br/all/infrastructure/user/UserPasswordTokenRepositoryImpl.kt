package br.all.infrastructure.user

import UserPasswordTokenEntity
import br.all.application.user.repository.UserPasswordTokenDto
import br.all.application.user.repository.UserPasswordTokenRepository
import org.springframework.data.domain.Example
import java.util.Optional
import java.util.UUID

class UserPasswordTokenRepositoryImpl(private val userPasswordTokenEntity: JpaUserAccountRepository): UserPasswordTokenRepository {
    override fun save(dto: UserPasswordTokenDto) {
        TODO("Not yet implemented")
    }

    override fun existsByEmail(email: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun deleteById(id: UUID) {
        TODO("Not yet implemented")
    }

    override fun findByEmail(email: String): UserPasswordTokenDto? {
        TODO("Not yet implemented")
    }

    override fun update(UserPasswordTokenDto: UserPasswordTokenDto) {
        TODO("Not yet implemented")
    }

}