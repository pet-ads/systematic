package br.all.application.agreement.visibility

import br.all.domain.model.agreement.AgreementVisibility
import java.util.UUID

interface GetAgreementVisibilityService {
    fun getVisibility(presenter: GetAgreementVisibilityPresenter, request: RequestModel)

    data class RequestModel(val userId: UUID, val systematicStudyId: UUID)

    data class ResponseModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val selection: AgreementVisibility,
        val extraction: AgreementVisibility,
    )
}