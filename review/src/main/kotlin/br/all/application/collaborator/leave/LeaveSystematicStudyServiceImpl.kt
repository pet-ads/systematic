package br.all.application.collaborator.leave

import br.all.application.collaborator.repository.CollaboratorRepository
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.toDto
import br.all.application.shared.service.AuthorizationService
import br.all.domain.shared.user.ResearcherId
import br.all.domain.shared.user.Role
import org.springframework.transaction.annotation.Transactional

@Transactional
class LeaveSystematicStudyServiceImpl(
    private val collaboratorRepository: CollaboratorRepository,
    private val systematicStudyRepository: SystematicStudyRepository,
    private val authorizationService: AuthorizationService
): LeaveSystematicStudyService {
    override fun leave(
        presenter: LeaveSystematicStudyPresenter,
        request: LeaveSystematicStudyService.RequestModel
    ) {
        val context = authorizationService.authorize(presenter, request.userId, request.systematicStudyId, setOf(Role.EDITOR, Role.VIEWER, Role.REVIEWER))
        if (presenter.isDone()) return
        context!!

        if (request.userId == context.systematicStudy.owner.value) {
            presenter.prepareFailView(IllegalArgumentException("You must transfer ownership before leaving"))
            return
        }

        context.systematicStudy.removeCollaborator(ResearcherId(request.userId))
        systematicStudyRepository.saveOrUpdate(context.systematicStudy.toDto())

        collaboratorRepository.deleteByResearcherIdAndSystematicStudyId(request.userId, request.systematicStudyId)

        presenter.prepareSuccessView(LeaveSystematicStudyService.ResponseModel())
    }
}