package br.all.application.question.repository

import br.all.application.question.create.QuestionDTO
import br.all.domain.model.protocol.ProtocolId
import br.all.domain.model.question.QuestionId
import java.util.UUID

interface QuestionRepository {
    fun createOrUpdate(dto: QuestionDTO): Any
    fun findById(systematicStudyId: UUID, id: QuestionId): QuestionDTO?
    fun findAllByProtocol(systematicStudyId: UUID, protocolId: ProtocolId): List<QuestionDTO>
}