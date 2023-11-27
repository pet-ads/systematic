package br.all.application.protocol.util

import br.all.application.protocol.repository.ProtocolDto
import br.all.application.protocol.repository.ProtocolRepository
import java.util.*

class FakeProtocolRepository: ProtocolRepository {
    private val db = mutableMapOf<UUID, ProtocolDto>()

    override fun create(dto: ProtocolDto) = run { db[dto.id] = dto }

    override fun findById(id: UUID) = db[id]

    override fun findBySystematicStudy(systematicStudyId: UUID) = db.values.find { it.systematicStudy == systematicStudyId }

    override fun findAll() = db.values.toList()

    override fun existsById(id: UUID) = id in db

    override fun existsBySystematicStudy(systematicStudyId: UUID) = db.values.any { it.systematicStudy == systematicStudyId }
}