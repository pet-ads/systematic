package br.all.application.question.repository

import java.util.UUID

interface QuestionRepository {
    fun createOrUpdate(dto: QuestionDto)
    fun findById(systematicStudyId: UUID, id: UUID): QuestionDto?
    fun findAllBySystematicStudyId(systematicStudyId: UUID, context: String? = null): List<QuestionDto>
}