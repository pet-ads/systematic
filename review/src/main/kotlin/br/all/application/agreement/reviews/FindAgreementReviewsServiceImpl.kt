package br.all.application.agreement.reviews

import br.all.application.agreement.reviews.FindAgreementReviewsService.RequestModel
import br.all.application.agreement.reviews.FindAgreementReviewsService.ResponseModel
import br.all.application.agreement.reviews.FindAgreementReviewsService.ReviewerVote
import br.all.application.agreement.reviews.FindAgreementReviewsService.StudyVotes
import br.all.application.agreement.visibility.AgreementVisibilityPolicy
import br.all.application.reviewmode.repository.ReviewerStudyDto
import br.all.application.reviewmode.repository.ReviewerStudySearchRepository
import br.all.application.shared.service.AuthorizationService
import br.all.domain.model.study.StudyReviewStage
import br.all.domain.shared.user.Role

class FindAgreementReviewsServiceImpl(
    private val authorizationService: AuthorizationService,
    private val agreementVisibilityPolicy: AgreementVisibilityPolicy,
    private val reviewerStudySearchRepository: ReviewerStudySearchRepository,
) : FindAgreementReviewsService {

    override fun findReviews(presenter: FindAgreementReviewsPresenter, request: RequestModel) {
        val context = authorizationService.authorize(
            presenter, request.userId, request.systematicStudyId, Role.entries.toSet()
        ) ?: return

        val ids = request.studyReviewIds.distinct()
        if (ids.isEmpty() || ids.size > MAX_STUDIES) {
            presenter.prepareFailView(
                IllegalArgumentException("Inform between 1 and $MAX_STUDIES study review ids.")
            )
            return
        }

        val decision = agreementVisibilityPolicy.evaluate(
            request.userId, request.systematicStudyId, request.stage, context.role
        )

        val votesByStudy: Map<Long, List<ReviewerStudyDto>> =
            if (decision.allowed)
                reviewerStudySearchRepository
                    .findAllByStudyIds(request.systematicStudyId, request.stage, ids)
                    .groupBy { it.studyId }
            else emptyMap()

        val studies = ids.map { studyId ->
            StudyVotes(
                studyId,
                votesByStudy[studyId].orEmpty()
                    .sortedBy { it.researcherId }
                    .map { toVote(it, request.stage) }
            )
        }

        presenter.prepareSuccessView(
            ResponseModel(
                userId = request.userId,
                systematicStudyId = request.systematicStudyId,
                stage = request.stage,
                visibility = decision.visibility,
                canSeeReviews = decision.allowed,
                reason = decision.reason,
                studies = studies,
            )
        )
    }

    private fun toVote(copy: ReviewerStudyDto, stage: StudyReviewStage): ReviewerVote {
        val study = copy.studyReview
        return ReviewerVote(
            reviewerId = copy.researcherId,
            status = if (stage == StudyReviewStage.SELECTION) study.selectionStatus else study.extractionStatus,
            criteria = if (stage == StudyReviewStage.SELECTION) study.selectionCriteria else study.extractionCriteria,
            readingPriority = study.readingPriority,
            comments = study.comments,
            formAnswers = study.formAnswers,
            robAnswers = study.robAnswers,
        )
    }

    companion object {
        const val MAX_STUDIES = 100
    }
}