package br.all.application.question.repository

import br.all.application.question.create.QuestionDTO
import br.all.domain.model.protocol.ProtocolId
import br.all.domain.model.question.QuestionId

interface QuestionRepository {
    fun create(dto: QuestionDTO)
    fun findById(id: QuestionId): QuestionDTO
    fun findAllByProtocol(protocolId: ProtocolId): List<QuestionDTO>
    fun update(dto: QuestionDTO)
}