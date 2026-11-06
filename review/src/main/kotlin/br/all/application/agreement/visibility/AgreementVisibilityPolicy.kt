package br.all.application.agreement.visibility

import br.all.application.agreement.repository.AgreementVisibilityRepository
import br.all.application.agreement.repository.findOrDefault
import br.all.application.reviewmode.repository.ReviewerStudySearchRepository
import br.all.domain.model.agreement.AgreementVisibility
import br.all.domain.model.study.StudyReviewStage
import br.all.domain.shared.user.Role
import java.util.UUID

class AgreementVisibilityPolicy(
    private val agreementVisibilityRepository: AgreementVisibilityRepository,
    private val reviewerStudySearchRepository: ReviewerStudySearchRepository,
) {
    enum class Reason { HIDDEN, NOT_FINISHED }

    data class Decision(
        val visibility: AgreementVisibility,
        val allowed: Boolean,
        val reason: Reason? = null,
    )

    fun evaluate(userId: UUID, systematicStudyId: UUID, stage: StudyReviewStage, role: Role): Decision {
        val visibility = agreementVisibilityRepository.findOrDefault(systematicStudyId).of(stage)

        if (role in FULL_ACCESS_ROLES) return Decision(visibility, allowed = true)

        return when (visibility) {
            AgreementVisibility.ALL -> Decision(visibility, allowed = true)
            AgreementVisibility.NONE -> Decision(visibility, allowed = false, reason = Reason.HIDDEN)
            AgreementVisibility.FINISHED_ONLY ->
                if (hasFinished(userId, systematicStudyId, stage)) Decision(visibility, allowed = true)
                else Decision(visibility, allowed = false, reason = Reason.NOT_FINISHED)
        }
    }

    fun hasFinished(userId: UUID, systematicStudyId: UUID, stage: StudyReviewStage): Boolean {
        val assigned = reviewerStudySearchRepository.countAssigned(systematicStudyId, userId, stage)
        if (assigned == 0L) return false

        val unclassified = reviewerStudySearchRepository.countByStatus(
            systematicStudyId, userId, stage, statusAttributeOf(stage), UNCLASSIFIED
        )
        return unclassified == 0L
    }

    private fun statusAttributeOf(stage: StudyReviewStage) = when (stage) {
        StudyReviewStage.SELECTION -> "selectionStatus"
        StudyReviewStage.EXTRACTION -> "extractionStatus"
    }

    companion object {
        val FULL_ACCESS_ROLES = setOf(Role.OWNER, Role.ADMIN)
        private const val UNCLASSIFIED = "UNCLASSIFIED"
    }
}