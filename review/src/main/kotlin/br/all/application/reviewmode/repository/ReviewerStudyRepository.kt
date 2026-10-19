package br.all.application.reviewmode.repository

import java.util.UUID
import br.all.domain.model.study.StudyReviewStage
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ReviewerStudyRepository {
    fun saveOrUpdate(dto: ReviewerStudyDto)

    fun findById(
        systematicStudyId: UUID,
        researcherId: UUID,
        studyId: Long,
        stage: StudyReviewStage,
    ): ReviewerStudyDto?

    fun findAllByReviewer(
        systematicStudyId: UUID,
        researcherId: UUID,
    ): List<ReviewerStudyDto>

    fun existsById(
        systematicStudyId: UUID,
        researcherId: UUID,
        studyId: Long,
        stage: StudyReviewStage,
    ): Boolean

    fun findAllByReviewerPaged(
        systematicStudyId: UUID,
        researcherId: UUID,
        stage: StudyReviewStage? = null,
        pageable: Pageable = Pageable.unpaged(),
    ): Page<ReviewerStudyDto>

    fun findAllBySystematicStudyIdAndStage(systematicStudyId: UUID, stage: StudyReviewStage): List<ReviewerStudyDto>
}
