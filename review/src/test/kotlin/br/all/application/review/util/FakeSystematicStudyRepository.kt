package br.all.application.review.util

import br.all.application.review.repository.SystematicStudyDto
import br.all.application.review.repository.SystematicStudyRepository
import java.util.*

class FakeSystematicStudyRepository : SystematicStudyRepository {
    private val db = mutableMapOf<UUID, SystematicStudyDto>()

    override fun create(systematicStudyDto: SystematicStudyDto) = run{ db[systematicStudyDto.id] = systematicStudyDto }

    override fun findById(systematicStudyId: UUID): SystematicStudyDto? = db[systematicStudyId]

    override fun findAll(): List<SystematicStudyDto> = db.values.toList()
}