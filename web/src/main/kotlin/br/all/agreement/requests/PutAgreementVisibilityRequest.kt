package br.all.agreement.requests

import br.all.application.agreement.visibility.SetAgreementVisibilityService
import br.all.domain.model.agreement.AgreementVisibility
import br.all.domain.model.study.StudyReviewStage
import java.util.UUID

data class PutAgreementVisibilityRequest(
    val stage: StudyReviewStage,
    val visibility: AgreementVisibility,
) {
    fun toRequestModel(userId: UUID, systematicStudyId: UUID) =
        SetAgreementVisibilityService.RequestModel(
            userId = userId,
            systematicStudyId = systematicStudyId,
            stage = stage,
            visibility = visibility,
        )
}