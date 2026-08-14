package br.all.infrastructure.collaborator

import br.all.application.collaborator.repository.CollaboratorTokenDto
import br.all.application.collaborator.repository.CollaboratorTokenRepository
import br.all.infrastructure.shared.toNullable
import org.springframework.stereotype.Repository
import java.util.*

@Repository
open class CollaboratorTokenRepositoryImpl(
    private val innerRepository: MongoCollaboratorTokenRepository,
) : CollaboratorTokenRepository {
    override fun saveOrUpdate(dto: CollaboratorTokenDto) {
        innerRepository.save(dto.toDocument())
    }

    override fun findById(id: UUID) =
        innerRepository.findById(id)
            .toNullable()
            ?.toDto()

    override fun findAllBySystematicStudyId(systematicStudyId: UUID): List<CollaboratorTokenDto> =
        innerRepository.findAllBySystematicStudyId(systematicStudyId)
            .map { it.toDto() }

    override fun findBySystematicStudyIdAndResearcherId(
        systematicStudyId: UUID,
        researcherId: UUID
    ): CollaboratorTokenDto? =
        innerRepository.findBySystematicStudyIdAndResearcherId(systematicStudyId, researcherId)?.toDto()

    override fun existsById(id: UUID) = innerRepository.existsById(id)

    override fun deleteById(id: UUID) {
        innerRepository.deleteById(id)
    }
}