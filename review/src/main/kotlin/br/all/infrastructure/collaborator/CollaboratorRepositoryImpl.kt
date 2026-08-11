package br.all.infrastructure.collaborator

import br.all.application.collaborator.repository.CollaboratorDto
import br.all.application.collaborator.repository.CollaboratorRepository
import br.all.infrastructure.shared.toNullable
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class CollaboratorRepositoryImpl(
    private val innerRepository: MongoCollaboratorRepository,
) : CollaboratorRepository {
    override fun saveOrUpdate(dto: CollaboratorDto) {
        innerRepository.save(dto.toDocument())
    }

    override fun findById(id: UUID) = innerRepository.findById(id)
        .toNullable()
        ?.toDto()

    override fun findAll(systematicStudyId: UUID): List<CollaboratorDto> =
        innerRepository.findAllBySystematicStudyId(systematicStudyId).map { it.toDto() }


    override fun existsById(id: UUID) = innerRepository.existsById(id)


    override fun existsByResearcherIdAndSystematicStudyId(id: UUID, systematicStudyId: UUID) =
        innerRepository.existsByResearcherIdAndSystematicStudyId(id, systematicStudyId)

    override fun findByResearcherIdAndSystematicStudyId(
        id: UUID,
        systematicStudyId: UUID
    ) =  innerRepository.findByResearcherIdAndSystematicStudyId(id, systematicStudyId)?.toDto()

    override fun delete(dto: CollaboratorDto) {
        innerRepository.delete(dto.toDocument())
    }

    override fun deleteByResearcherIdAndSystematicStudyId(id: UUID, systematicStudyId: UUID) {
        innerRepository.deleteByResearcherIdAndSystematicStudyId(id, systematicStudyId)
    }

}