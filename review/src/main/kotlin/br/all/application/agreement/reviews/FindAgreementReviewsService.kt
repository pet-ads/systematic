package br.all.application.agreement.reviews

import br.all.application.agreement.visibility.AgreementVisibilityPolicy
import br.all.domain.model.agreement.AgreementVisibility
import br.all.domain.model.study.StudyReviewStage
import java.util.UUID

interface FindAgreementReviewsService {
    fun findReviews(presenter: FindAgreementReviewsPresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val stage: StudyReviewStage,
        val studyReviewIds: List<Long>,
    )

    data class ReviewerVote(
        val reviewerId: UUID,
        val status: String,
        val criteria: Set<String>,
        val readingPriority: String,
        val comments: String,
        val formAnswers: Map<UUID, String>,
        val robAnswers: Map<UUID, String>,
    )

    data class StudyVotes(
        val studyReviewId: Long,
        val reviews: List<ReviewerVote>,
    )

    data class ResponseModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val stage: StudyReviewStage,
        val visibility: AgreementVisibility,
        val canSeeReviews: Boolean,
        val reason: AgreementVisibilityPolicy.Reason?,
        val studies: List<StudyVotes>,
    )
}