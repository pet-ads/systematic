package br.all.domain.model.reviewmode

import br.all.domain.model.review.SystematicStudyId
import br.all.domain.model.study.StudyReviewStage
import br.all.domain.shared.ddd.Entity
import java.util.UUID

class SystematicStudyReviewMode(
    val systematicStudyId: SystematicStudyId,
    selection: ReviewMode? = null,
    extraction: ReviewMode? = null
) : Entity<UUID>(systematicStudyId) {

    var selection: ReviewMode? = selection
        private set

    var extraction: ReviewMode? = extraction
        private set

    fun defineMode(stage: StudyReviewStage, mode: ReviewMode) {
        when (stage) {
            StudyReviewStage.SELECTION -> this.selection = mode
            StudyReviewStage.EXTRACTION -> this.extraction = mode
        }
    }
}
