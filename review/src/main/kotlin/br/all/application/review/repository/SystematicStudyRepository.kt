package br.all.application.review.repository

import java.util.*

interface SystematicStudyRepository {
    fun create(systematicStudyDto: SystematicStudyDto)

    fun findById(systematicStudyId : UUID) : Optional<SystematicStudyDto>

    fun findAll() : List<SystematicStudyDto>
}