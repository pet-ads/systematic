package br.all.application.collaborator.remove

import br.all.application.review.repository.CollaboratorRepository
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.toDto
import br.all.application.shared.service.AuthorizationService
import br.all.domain.shared.exception.EntityNotFoundException
import br.all.domain.shared.user.ResearcherId
import br.all.domain.shared.user.Role
import org.springframework.transaction.annotation.Transactional

@Transactional
class RemoveCollaboratorServiceImpl(
    private val collaboratorRepository: CollaboratorRepository,
    private val systematicStudyRepository: SystematicStudyRepository,
    private val authorizationService: AuthorizationService
): RemoveCollaboratorService {
    override fun remove(
        presenter: RemoveCollaboratorPresenter,
        request: RemoveCollaboratorService.RequestModel
    ) {
        val context = authorizationService.authorize(presenter, request.userId, request.systematicStudyId, setOf(Role.OWNER))
        if (presenter.isDone()) return
        context!!

        if (request.researcherId == request.userId) {
            presenter.prepareFailView(IllegalArgumentException("You cannot self remove on this endpoint."))
            return
        }

        val collaborator = collaboratorRepository.findByResearcherIdAndSystematicStudyId(request.researcherId, request.systematicStudyId)
        if(collaborator == null) {
            presenter.prepareFailView(EntityNotFoundException("There is no collaborator of id ${request.researcherId} on this systematic study."))
            return
        }

        context.systematicStudy.removeCollaborator(ResearcherId(request.researcherId))
        systematicStudyRepository.saveOrUpdate(context.systematicStudy.toDto())

        collaboratorRepository.delete(collaborator)

        presenter.prepareSuccessView(RemoveCollaboratorService.ResponseModel())
    }
}