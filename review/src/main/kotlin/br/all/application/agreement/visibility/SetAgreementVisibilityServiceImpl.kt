package br.all.application.agreement.visibility

import br.all.application.agreement.repository.AgreementVisibilityRepository
import br.all.application.agreement.repository.findOrDefault
import br.all.application.agreement.visibility.SetAgreementVisibilityService.RequestModel
import br.all.application.agreement.visibility.SetAgreementVisibilityService.ResponseModel
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.user.CredentialsService
import br.all.domain.model.review.SystematicStudy
import br.all.domain.shared.exception.UnauthorizedUserException
import br.all.domain.shared.user.ResearcherId
import br.all.domain.shared.user.Role

class SetAgreementVisibilityServiceImpl(
    private val systematicStudyRepository: SystematicStudyRepository,
    private val agreementVisibilityRepository: AgreementVisibilityRepository,
    private val credentialsService: CredentialsService,
) : SetAgreementVisibilityService {

    override fun setVisibility(presenter: SetAgreementVisibilityPresenter, request: RequestModel) {
        val user = credentialsService.loadCredentials(request.userId)?.toUser()
        val systematicStudy = systematicStudyRepository.findById(request.systematicStudyId)
            ?.let { SystematicStudy.fromDto(it) }

        presenter.prepareIfFailsPreconditions(user, systematicStudy)
        if (presenter.isDone()) return

        val isOwner = requireNotNull(systematicStudy).owner == ResearcherId(request.userId)
        val isAdmin = Role.ADMIN in requireNotNull(user).roles
        if (!isOwner && !isAdmin) {
            presenter.prepareFailView(
                UnauthorizedUserException("User of id ${request.userId} can not change the agreement visibility.")
            )
            return
        }

        val updated = agreementVisibilityRepository.findOrDefault(request.systematicStudyId)
            .with(request.stage, request.visibility)
        agreementVisibilityRepository.saveOrUpdate(updated)

        presenter.prepareSuccessView(
            ResponseModel(request.userId, request.systematicStudyId, updated.selection, updated.extraction)
        )
    }
}