package br.all.application.review.repository

import java.util.*

interface SystematicStudyRepository {
    fun saveOrUpdate(dto: SystematicStudyDto)

    fun findById(id : UUID) : SystematicStudyDto?

    fun findSomeByCollaborator(researcherId: UUID): List<SystematicStudyDto>

    fun findAll() : List<SystematicStudyDto>

    fun existsById(id: UUID) : Boolean
    fun hasReviewer(reviewId: UUID, researcherId: UUID): Boolean
}