package br.all.infrastructure.protocol

import br.all.application.protocol.repository.ProtocolDto
import br.all.application.protocol.repository.ProtocolRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
open class ProtocolRepositoryImpl(private val innerRepository: MongoProtocolRepository): ProtocolRepository {
    override fun saveOrUpdate(dto: ProtocolDto) {
        innerRepository.save(dto.toDocument())
    }

    override fun findById(id: UUID): ProtocolDto? {
        TODO("Not yet implemented")
    }

    override fun findBySystematicStudy(systematicStudyId: UUID): ProtocolDto? {
        TODO("Not yet implemented")
    }

    override fun findAll(): List<ProtocolDto> {
        TODO("Not yet implemented")
    }

    override fun existsById(id: UUID): Boolean {
        TODO("Not yet implemented")
    }

    override fun existsBySystematicStudy(systematicStudyId: UUID): Boolean {
        TODO("Not yet implemented")
    }
}
