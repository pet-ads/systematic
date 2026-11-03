package br.all.application.study.access

import br.all.application.reviewmode.repository.ReviewerStudyRepository
import br.all.application.study.repository.StudyReviewDto
import br.all.application.study.repository.StudyReviewRepository
import br.all.domain.model.study.StudyReviewStage
import br.all.domain.shared.exception.UnauthorizedUserException
import java.util.UUID

class StudyReviewAccess(
    private val studyReviewRepository: StudyReviewRepository,
    private val reviewerStudyRepository: ReviewerStudyRepository? = null,
) {
    class Target(val dto: StudyReviewDto, private val persist: (StudyReviewDto) -> Unit) {
        fun save(updated: StudyReviewDto) = persist(updated)
    }

    fun validate(userId: UUID, reviewerId: UUID?, stage: StudyReviewStage?): Throwable? = when {
        reviewerId == null -> null
        !isOwnCopy(userId, reviewerId) ->
            UnauthorizedUserException("A reviewer can only access the studies assigned to himself.")
        stage == null -> IllegalArgumentException("The stage is required when the reviewer is informed.")
        else -> null
    }

    fun isOwnCopy(userId: UUID, reviewerId: UUID?): Boolean = reviewerId == null || reviewerId == userId

    fun open(systematicStudyId: UUID, studyId: Long, reviewerId: UUID?, stage: StudyReviewStage?): Target? {
        if (reviewerId == null) {
            val dto = studyReviewRepository.findById(systematicStudyId, studyId) ?: return null
            return Target(dto) { studyReviewRepository.saveOrUpdate(it) }
        }

        requireNotNull(stage) { "The stage is required when the reviewer is informed." }
        val repository = checkNotNull(reviewerStudyRepository) {
            "ReviewerStudyRepository is required to work with a reviewer's copy."
        }
        val copy = repository.findById(systematicStudyId, reviewerId, studyId, stage) ?: return null
        return Target(copy.studyReview) { repository.saveOrUpdate(copy.copy(studyReview = it)) }
    }
}