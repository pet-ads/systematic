package br.all.infrastructure.question

import br.all.application.question.create.QuestionDTO
import br.all.application.question.repository.QuestionRepository
import br.all.domain.model.protocol.ProtocolId
import br.all.domain.model.question.QuestionId
import br.all.infrastructure.shared.toNullable
import java.util.*

class QuestionRepositoryImpl(private val repository: MongoQuestionRepository): QuestionRepository {
    override fun createOrUpdate(dto: QuestionDTO): QuestionDocument = repository.save(dto.toDocument())

    override fun findById(systematicStudyId: UUID, id: QuestionId) = repository.findById(id).toNullable()?.toDto()

    override fun findAllByProtocol(systematicStudyId: UUID, protocolId: ProtocolId) =
        repository.findAllById_QuestionId(protocolId).map { it.toDto() }
}