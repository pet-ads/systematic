package br.all.infrastructure.protocol

import br.all.application.protocol.repository.ProtocolDto
import br.all.application.protocol.repository.ProtocolRepository
import br.all.infrastructure.search.toDto
import br.all.infrastructure.shared.toNullable
import org.springframework.stereotype.Repository
import java.util.*

@Repository
open class ProtocolRepositoryImpl(private val innerRepository: MongoProtocolRepository): ProtocolRepository {
    override fun saveOrUpdate(dto: ProtocolDto) {
        innerRepository.save(dto.toDocument())
    }

    override fun findById(id: UUID) = innerRepository.findById(id)
        .toNullable()
        ?.toDto()

    override fun existsById(id: UUID) = innerRepository.existsById(id)

    override fun findBySystematicStudyId(systematicStudy: UUID): ProtocolDto? {
        return innerRepository.findBySystematicStudyId(systematicStudy).toDto()
    }
}
