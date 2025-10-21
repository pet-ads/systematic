package br.all.application.question.repository

import br.all.domain.model.question.QuestionContextEnum
import java.util.UUID

interface QuestionRepository {
    fun createOrUpdate(dto: QuestionDto)
    fun findById(systematicStudyId: UUID, id: UUID): QuestionDto?
    fun findAllBySystematicStudyId(systematicStudyId: UUID, context: QuestionContextEnum? = null): List<QuestionDto>
    fun deleteById(systematicStudyId: UUID, id: UUID)
}