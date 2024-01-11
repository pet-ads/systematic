package br.all.application.protocol.find

import br.all.application.protocol.repository.ProtocolDto
import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.review.repository.SystematicStudyRepository
import br.all.domain.shared.utils.exists
import java.util.*

class FindOneProtocolServiceImpl(
    private val protocolRepository: ProtocolRepository,
    private val systematicStudyRepository: SystematicStudyRepository,
): FindOneProtocolService {
    override fun findById(id: UUID) = protocolRepository.findById(id)

    override fun findBySystematicStudy(reviewId: UUID): ProtocolDto? {
        exists(systematicStudyRepository.existsById(reviewId))
            { "Cannot find a protocol because there is not a SystematicStudy with id: $reviewId" }
        return protocolRepository.findBySystematicStudy(reviewId)
    }

    override fun existsById(id: UUID) = protocolRepository.existsById(id)

    override fun existsBySystematicStudy(reviewId: UUID): Boolean {
        exists(systematicStudyRepository.existsById(reviewId))
            { "There is not a SystematicStudy with id: $reviewId" }
        return protocolRepository.existsBySystematicStudy(reviewId)
    }
}