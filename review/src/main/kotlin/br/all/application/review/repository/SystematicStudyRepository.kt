package br.all.application.review.repository

import java.util.*

interface SystematicStudyRepository {
    fun create(systematicStudyDto: SystematicStudyDto)

    fun findById(systematicStudyId : UUID) : SystematicStudyDto?

    fun findAll() : List<SystematicStudyDto>

    fun existsById(systematicStudyId: UUID) : Boolean
}