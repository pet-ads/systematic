package br.all.application.agreement.repository

import br.all.domain.model.agreement.AgreementVisibility
import br.all.domain.model.study.StudyReviewStage
import java.util.UUID

data class AgreementVisibilityDto(
    val systematicStudyId: UUID,
    val selection: AgreementVisibility = AgreementVisibility.DEFAULT,
    val extraction: AgreementVisibility = AgreementVisibility.DEFAULT,
) {
    fun of(stage: StudyReviewStage): AgreementVisibility = when (stage) {
        StudyReviewStage.SELECTION -> selection
        StudyReviewStage.EXTRACTION -> extraction
    }

    fun with(stage: StudyReviewStage, visibility: AgreementVisibility): AgreementVisibilityDto = when (stage) {
        StudyReviewStage.SELECTION -> copy(selection = visibility)
        StudyReviewStage.EXTRACTION -> copy(extraction = visibility)
    }
}