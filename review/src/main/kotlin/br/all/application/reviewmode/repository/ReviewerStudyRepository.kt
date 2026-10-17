package br.all.application.reviewmode.repository

import java.util.UUID

interface ReviewerStudyRepository {
    fun saveOrUpdate(dto: ReviewerStudyDto)

    fun findById(
        systematicStudyId: UUID,
        researcherId: UUID,
        studyId: Long,
    ): ReviewerStudyDto?

    fun findAllByReviewer(
        systematicStudyId: UUID,
        researcherId: UUID,
    ): List<ReviewerStudyDto>

    fun existsById(
        systematicStudyId: UUID,
        researcherId: UUID,
        studyId: Long,
    ): Boolean
}
