package br.all.application.reviewmode.repository

import java.util.UUID
import br.all.domain.model.study.StudyReviewStage

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

    fun findAllBySystematicStudyIdAndStage(systematicStudyId: UUID, stage: StudyReviewStage): List<ReviewerStudyDto>

    fun saveAll(dtos: List<ReviewerStudyDto>)
}
