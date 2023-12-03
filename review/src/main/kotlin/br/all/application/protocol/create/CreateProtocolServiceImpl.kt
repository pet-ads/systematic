package br.all.application.protocol.create

import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.protocol.repository.fromRequestModel
import br.all.application.protocol.repository.toDto
import br.all.application.protocol.shared.ProtocolResponseModel
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.shared.DuplicateElementException
import br.all.domain.model.protocol.Protocol
import br.all.domain.services.UuidGeneratorService
import br.all.domain.shared.utils.requireThatExists
import java.util.*

class CreateProtocolServiceImpl(
    private val protocolRepository: ProtocolRepository,
    private val systematicStudyRepository: SystematicStudyRepository,
): CreateProtocolService {
    override fun create(reviewId: UUID, requestModel: ProtocolRequestModel): ProtocolResponseModel {
        requireThatExists(systematicStudyRepository.existsById(reviewId))
            { "Unable to create a protocol for a nonexistent SystematicStudy! Provided study id: $reviewId" }

        if (protocolRepository.existsBySystematicStudy(reviewId))
            throw DuplicateElementException("There already is a protocol for this systematic study id: $reviewId")

        val protocol = Protocol.fromRequestModel(reviewId, requestModel)

        protocolRepository.create(protocol.toDto())

        return ProtocolResponseModel(reviewId, reviewId)
    }
}