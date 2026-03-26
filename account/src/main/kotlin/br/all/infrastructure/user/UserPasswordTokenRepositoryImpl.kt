package br.all.infrastructure.user

import br.all.application.user.repository.UserPasswordTokenDto
import br.all.application.user.repository.UserPasswordTokenRepository
import br.all.application.user.repository.toDto
import br.all.application.user.repository.toEntity
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class UserPasswordTokenRepositoryImpl(
    private val jpaRepository: JpaUserPasswordTokenRepository
) : UserPasswordTokenRepository {

    override fun save(dto: UserPasswordTokenDto): UserPasswordTokenDto {
        return jpaRepository.save(dto.toEntity()).toDto()
    }

    override fun existsByEmail(email: String): Boolean {
        return jpaRepository.findByEmailAndStatus(email, TokenStatus.PENDENTE) != null
    }

    override fun deleteById(id: UUID) {
        jpaRepository.deleteById(id)
    }

    override fun findByEmail(email: String): UserPasswordTokenDto? {
        return jpaRepository
            .findByEmailAndStatus(email, TokenStatus.PENDENTE)
            ?.toDto()
    }
}