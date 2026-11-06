package br.all.application.reviewmode.repository

import br.all.domain.model.study.StudyReviewStage
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface ReviewerStudySearchRepository {
    fun findAllByAdvancedSearch(
        systematicStudyId: UUID,
        reviewerId: UUID,
        stage: StudyReviewStage?,
        filters: Map<String, Any?>,
        pageable: Pageable,
    ): Page<ReviewerStudyDto>

    fun countAssigned(systematicStudyId: UUID, reviewerId: UUID, stage: StudyReviewStage): Long

    fun countByStatus(
        systematicStudyId: UUID,
        reviewerId: UUID,
        stage: StudyReviewStage,
        statusAttribute: String,
        status: String,
    ): Long

    fun findAllByStudyIds(
        systematicStudyId: UUID,
        stage: StudyReviewStage,
        studyIds: Collection<Long>,
    ): List<ReviewerStudyDto>
}