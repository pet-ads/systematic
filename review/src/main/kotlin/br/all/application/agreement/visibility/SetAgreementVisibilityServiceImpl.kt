package br.all.application.agreement.visibility

import br.all.application.agreement.repository.AgreementVisibilityRepository
import br.all.application.agreement.repository.findOrDefault
import br.all.application.agreement.visibility.SetAgreementVisibilityService.RequestModel
import br.all.application.agreement.visibility.SetAgreementVisibilityService.ResponseModel
import br.all.application.shared.service.AuthorizationService
import br.all.domain.shared.user.Role

class SetAgreementVisibilityServiceImpl(
    private val authorizationService: AuthorizationService,
    private val agreementVisibilityRepository: AgreementVisibilityRepository,
) : SetAgreementVisibilityService {

    override fun setVisibility(presenter: SetAgreementVisibilityPresenter, request: RequestModel) {
        authorizationService.authorize(
            presenter, request.userId, request.systematicStudyId, setOf(Role.OWNER)
        ) ?: return

        val updated = agreementVisibilityRepository.findOrDefault(request.systematicStudyId)
            .with(request.stage, request.visibility)
        agreementVisibilityRepository.saveOrUpdate(updated)

        presenter.prepareSuccessView(
            ResponseModel(request.userId, request.systematicStudyId, updated.selection, updated.extraction)
        )
    }
}