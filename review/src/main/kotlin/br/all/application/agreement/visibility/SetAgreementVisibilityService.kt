package br.all.application.agreement.visibility

import br.all.domain.model.agreement.AgreementVisibility
import br.all.domain.model.study.StudyReviewStage
import java.util.UUID

interface SetAgreementVisibilityService {
    fun setVisibility(presenter: SetAgreementVisibilityPresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val stage: StudyReviewStage,
        val visibility: AgreementVisibility,
    )

    data class ResponseModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val selection: AgreementVisibility,
        val extraction: AgreementVisibility,
    )
}