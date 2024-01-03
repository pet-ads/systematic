package br.all.infrastructure.review

import br.all.application.review.repository.SystematicStudyDto
import br.all.application.review.repository.SystematicStudyRepository
import br.all.infrastructure.shared.toNullable
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class SystematicStudyRepositoryImpl(
    private val innerRepository: MongoSystematicStudyRepository,
) : SystematicStudyRepository {
    override fun saveOrUpdate(dto: SystematicStudyDto) {
        innerRepository.save(dto.toDocument())
    }

    override fun findById(id: UUID) = innerRepository.findById(id)
        .toNullable()
        ?.toDto()

    override fun findAll() = innerRepository.findAll()
        .map { it.toDto() }

    override fun existsById(id: UUID) = innerRepository.existsById(id)

    override fun hasReviewer(reviewId: UUID, researcherId: UUID) = innerRepository.findById(reviewId)
        .toNullable()
        ?.let { it.owner == reviewId } ?: false
}