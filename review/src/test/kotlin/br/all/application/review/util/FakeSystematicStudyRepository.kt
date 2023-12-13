package br.all.application.review.util

import br.all.application.review.repository.SystematicStudyDto
import br.all.application.review.repository.SystematicStudyRepository
import org.springframework.stereotype.Repository
import java.util.*


class FakeSystematicStudyRepository : SystematicStudyRepository {
    private val db = mutableMapOf<UUID, SystematicStudyDto>()

    override fun saveOrUpdate(dto: SystematicStudyDto) = run{ db[dto.id] = dto }

    override fun findById(id: UUID): SystematicStudyDto? = db[id]

    override fun findAll(): List<SystematicStudyDto> = db.values.toList()

    override fun existsById(id: UUID) = id in db.keys
    override fun hasReviewer(reviewId: UUID, researcherId: UUID) = true
}