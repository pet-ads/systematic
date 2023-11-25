package br.all.infrastructure.review

import br.all.application.review.repository.SystematicStudyDto
import br.all.application.review.repository.SystematicStudyRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
open class FakeSystematicStudyRepository : SystematicStudyRepository {
    private val db = mutableMapOf<UUID, SystematicStudyDto>()

    override fun create(dto: SystematicStudyDto) = run{ db[dto.id] = dto }

    override fun findById(id: UUID): SystematicStudyDto? = db[id]

    override fun findAll(): List<SystematicStudyDto> = db.values.toList()

    override fun existsById(id: UUID) = true
    override fun hasReviewer(reviewId: UUID, researcherId: UUID) = true
}