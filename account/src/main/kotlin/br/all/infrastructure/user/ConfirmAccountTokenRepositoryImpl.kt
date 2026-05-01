package br.all.infrastructure.user

import br.all.application.user.repository.ConfirmAccountTokenDto
import br.all.application.user.repository.ConfirmAccountTokenRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class ConfirmAccountTokenRepositoryImpl(
    private val jpaRepository: JpaConfirmAccountTokenRepository
) : ConfirmAccountTokenRepository {

    override fun save(dto: ConfirmAccountTokenDto): ConfirmAccountTokenDto {
        return jpaRepository.save(dto.toEntity()).toDto()
    }

    override fun deleteByToken(token: UUID) {
        jpaRepository.deleteById(token)
    }

    override fun update(dto: ConfirmAccountTokenDto) {
        jpaRepository.save(dto.toEntity()).toDto()
    }

    override fun findByUserId(userId: UUID): ConfirmAccountTokenDto? {
        return jpaRepository
            .findByUserId(userId)?.toDto()
    }

    override fun findById(token: UUID): ConfirmAccountTokenDto? {
        return jpaRepository
            .findById(token).orElse(null)?.toDto()
    }

}