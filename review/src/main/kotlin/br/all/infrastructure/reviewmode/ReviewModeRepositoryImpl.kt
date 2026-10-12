package br.all.infrastructure.reviewmode

import br.all.application.reviewmode.repository.ReviewModeDto
import br.all.application.reviewmode.repository.ReviewModeRepository
import br.all.infrastructure.shared.toNullable
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
open class ReviewModeRepositoryImpl(
    private val innerRepository: MongoReviewModeRepository
) : ReviewModeRepository {

    override fun saveOrUpdate(dto: ReviewModeDto) {
        innerRepository.save(dto.toDocument())
    }

    override fun findById(systematicStudyId: UUID): ReviewModeDto? =
        innerRepository.findById(systematicStudyId)
            .toNullable()
            ?.toDto()

    override fun existsById(systematicStudyId: UUID): Boolean =
        innerRepository.existsById(systematicStudyId)
}
