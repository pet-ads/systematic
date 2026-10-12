package br.all.application.reviewmode.repository

import java.util.UUID

interface ReviewModeRepository {
    fun saveOrUpdate(dto: ReviewModeDto)

    fun findById(systematicStudyId: UUID): ReviewModeDto?

    fun existsById(systematicStudyId: UUID): Boolean
}
