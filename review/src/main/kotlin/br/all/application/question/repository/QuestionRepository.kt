package br.all.application.question.repository

import br.all.domain.model.protocol.ProtocolId
import br.all.domain.model.question.QuestionId
import java.util.UUID

interface QuestionRepository {
    fun createOrUpdate(dto: QuestionDto): Any
    fun findById(systematicStudyId: UUID, id: UUID): QuestionDto?
    fun findAllBySystematicStudyId(systematicStudyId: UUID): List<QuestionDto>
}