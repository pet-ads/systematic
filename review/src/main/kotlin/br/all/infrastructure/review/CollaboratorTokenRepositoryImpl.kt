package br.all.infrastructure.review

import br.all.application.review.repository.CollaboratorTokenDto
import br.all.application.review.repository.CollaboratorTokenRepository
import br.all.infrastructure.shared.toNullable
import org.springframework.stereotype.Repository
import java.util.*

@Repository
open class CollaboratorTokenRepositoryImpl(
    private val innerRepository: MongoCollaboratorTokenRepository,
) : CollaboratorTokenRepository {
    override fun saveOrUpdate(dto: CollaboratorTokenDto) {
        innerRepository.save(dto.toDocument())
    }

    override fun findById(id: UUID) =
        innerRepository.findById(id)
            .toNullable()
            ?.toDto()


    override fun existsById(id: UUID) = innerRepository.existsById(id)
}