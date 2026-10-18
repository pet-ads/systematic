package br.all.domain.model.reviewmode

import br.all.domain.model.review.SystematicStudyId
import br.all.domain.model.study.StudyReviewStage
import br.all.domain.shared.ddd.Entity
import br.all.domain.shared.user.ResearcherId
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

    data class SplitParams(
        val reviewers: List<ResearcherId>,
        val reviewPerStudy: Int,
    )

    fun defineMode(stage: StudyReviewStage, mode: ReviewMode) {
        when (stage) {
            StudyReviewStage.SELECTION -> this.selection = mode
            StudyReviewStage.EXTRACTION -> this.extraction = mode
        }
    }

    fun defineMode(stage: StudyReviewStage, mainReviewer: ResearcherId) =
        defineMode(stage, Replication(mainReviewer))

    fun defineMode(stage: StudyReviewStage, params: SplitParams) =
        defineMode(stage, Split(params.reviewers, params.reviewPerStudy))
}
