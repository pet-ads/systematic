package br.all.application.agreement.repository

import java.util.UUID

interface AgreementVisibilityRepository {
    fun findById(systematicStudyId: UUID): AgreementVisibilityDto?

    fun saveOrUpdate(dto: AgreementVisibilityDto)
}

fun AgreementVisibilityRepository.findOrDefault(systematicStudyId: UUID): AgreementVisibilityDto =
    findById(systematicStudyId) ?: AgreementVisibilityDto(systematicStudyId)