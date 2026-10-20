package br.all.application.agreement.visibility

import br.all.application.agreement.repository.AgreementVisibilityRepository
import br.all.application.agreement.repository.findOrDefault
import br.all.application.agreement.visibility.GetAgreementVisibilityService.RequestModel
import br.all.application.agreement.visibility.GetAgreementVisibilityService.ResponseModel
import br.all.application.shared.service.AuthorizationService
import br.all.domain.shared.user.Role

class GetAgreementVisibilityServiceImpl(
    private val authorizationService: AuthorizationService,
    private val agreementVisibilityRepository: AgreementVisibilityRepository,
) : GetAgreementVisibilityService {

    override fun getVisibility(presenter: GetAgreementVisibilityPresenter, request: RequestModel) {
        authorizationService.authorize(
            presenter, request.userId, request.systematicStudyId, Role.entries.toSet()
        ) ?: return

        val visibility = agreementVisibilityRepository.findOrDefault(request.systematicStudyId)
        presenter.prepareSuccessView(
            ResponseModel(request.userId, request.systematicStudyId, visibility.selection, visibility.extraction)
        )
    }
}