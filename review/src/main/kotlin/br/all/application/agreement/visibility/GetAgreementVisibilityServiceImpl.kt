package br.all.application.agreement.visibility

import br.all.application.agreement.repository.AgreementVisibilityRepository
import br.all.application.agreement.repository.findOrDefault
import br.all.application.agreement.visibility.GetAgreementVisibilityService.RequestModel
import br.all.application.agreement.visibility.GetAgreementVisibilityService.ResponseModel
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.user.CredentialsService
import br.all.domain.model.review.SystematicStudy

class GetAgreementVisibilityServiceImpl(
    private val systematicStudyRepository: SystematicStudyRepository,
    private val agreementVisibilityRepository: AgreementVisibilityRepository,
    private val credentialsService: CredentialsService,
) : GetAgreementVisibilityService {

    override fun getVisibility(presenter: GetAgreementVisibilityPresenter, request: RequestModel) {
        val user = credentialsService.loadCredentials(request.userId)?.toUser()
        val systematicStudy = systematicStudyRepository.findById(request.systematicStudyId)
            ?.let { SystematicStudy.fromDto(it) }

        presenter.prepareIfFailsPreconditions(user, systematicStudy)
        if (presenter.isDone()) return

        val visibility = agreementVisibilityRepository.findOrDefault(request.systematicStudyId)
        presenter.prepareSuccessView(
            ResponseModel(request.userId, request.systematicStudyId, visibility.selection, visibility.extraction)
        )
    }
}