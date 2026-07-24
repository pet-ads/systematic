package br.all.application.collaborator.update

import br.all.application.review.repository.CollaboratorRepository
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.toDto
import br.all.application.shared.service.AuthorizationService
import br.all.domain.shared.exception.EntityNotFoundException
import br.all.domain.shared.user.ResearcherId
import br.all.domain.shared.user.Role
import org.springframework.transaction.annotation.Transactional

@Transactional
class PassOwnershipServiceImpl(
    private val collaboratorRepository: CollaboratorRepository,
    private val systematicStudyRepository: SystematicStudyRepository,
    private val authorizationService: AuthorizationService
): PassOwnershipService {
    override fun pass(
        presenter: PassOwnershipPresenter,
        request: PassOwnershipService.RequestModel
    ) {
        val context = authorizationService.authorize(presenter, request.userId, request.systematicStudyId, setOf(Role.OWNER))
        if (presenter.isDone()) return
        context!!

        if (request.newOwnerId == request.userId) {
            presenter.prepareFailView(IllegalArgumentException("You are already the owner."))
            return
        }

        val newOwner = collaboratorRepository.findByResearcherIdAndSystematicStudyId(request.newOwnerId, request.systematicStudyId)
        if (newOwner == null) {
            presenter.prepareFailView(EntityNotFoundException("There is no collaborator of id ${request.newOwnerId} on this systematic study."))
            return
        }

        val oldOwner = collaboratorRepository.findByResearcherIdAndSystematicStudyId(request.userId, request.systematicStudyId)
        if (oldOwner == null) {
            presenter.prepareFailView(EntityNotFoundException("There is no collaborator of id ${request.userId} on this systematic study."))
            return
        }

        newOwner.role = Role.OWNER.name
        oldOwner.role = Role.EDITOR.name

        context.systematicStudy.changeOwner(ResearcherId(request.newOwnerId))
        systematicStudyRepository.saveOrUpdate(context.systematicStudy.toDto())

        collaboratorRepository.saveOrUpdate(newOwner )
        collaboratorRepository.saveOrUpdate(oldOwner )

        presenter.prepareSuccessView(PassOwnershipService.ResponseModel(request.systematicStudyId, request.newOwnerId))
    }
}