package br.all.application.review.create

import br.all.application.review.repository.CollaboratorDto
import br.all.application.review.repository.CollaboratorRepository
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.user.CredentialsService
import br.all.domain.shared.exception.EntityNotFoundException
import br.all.domain.shared.exception.UniquenessViolationException
import br.all.domain.shared.user.Role
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class AddCollaboratorService(
    private val systematicStudyRepository: SystematicStudyRepository,
    private val collaboratorRepository: CollaboratorRepository,
    private val credentialsService: CredentialsService
) {
    @Transactional
    fun addCollaborator(researcherId: UUID, systematicStudyId: UUID, role: Role) {
        val user = credentialsService.loadEnabledCredentialsById(researcherId)
            ?: throw EntityNotFoundException("User does not exist")

        val systematicStudy = systematicStudyRepository.findById(systematicStudyId)
            ?: throw EntityNotFoundException("Systematic study does not exist")

        if(collaboratorRepository.existsByResearcherIdAndSystematicStudyId(researcherId, systematicStudyId))
            throw UniquenessViolationException("Collaborator already exists on systematic study")

        if (systematicStudy.collaborators.contains(researcherId))
            throw UniquenessViolationException("Collaborator already exists on systematic study")

        systematicStudy.collaborators += researcherId
        systematicStudyRepository.saveOrUpdate(systematicStudy)

        val collaborator = CollaboratorDto(systematicStudyId, researcherId, user.name, user.email, role.toString())
        collaboratorRepository.saveOrUpdate(collaborator)
    }
}