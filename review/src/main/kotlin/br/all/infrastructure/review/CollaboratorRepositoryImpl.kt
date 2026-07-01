package br.all.infrastructure.review

import br.all.application.review.repository.CollaboratorDto
import br.all.application.review.repository.CollaboratorRepository
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


    override fun existsById(id: UUID) = innerRepository.existsById(id)


    override fun existsByResearcherIdAndSystematicStudyId(id: UUID, systematicStudyId: UUID) =
        innerRepository.existsByResearcherIdAndSystematicStudyId(id, systematicStudyId)
}