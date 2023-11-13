package br.all.application.protocol.find

import br.all.application.protocol.repository.ProtocolRepository
import java.util.*

class FindOneProtocolServiceImpl(val protocolRepository: ProtocolRepository): FindOneProtocolService {
    override fun findById(id: UUID) = protocolRepository.findById(id)

    override fun findBySystematicStudy(reviewId: UUID) = protocolRepository.findBySystematicStudy(reviewId)

    override fun existsById(id: UUID) = protocolRepository.existsById(id)

    override fun existsBySystematicStudy(reviewId: UUID) = protocolRepository.existsBySystematicStudy(reviewId)
}