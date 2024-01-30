package br.all.application.question.create

import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.protocol.repository.fromDto
import br.all.application.protocol.repository.toDto
import br.all.domain.model.protocol.Protocol
import br.all.domain.model.question.QuestionId
import java.util.*

class CreateExtractionQuestionStrategy(private val protocolRepository: ProtocolRepository) : CreateQuestionStrategy {
    override fun addQuestion(protocolId: UUID, questionId: UUID) {
        protocolRepository.findById(protocolId)?.let { dto ->
            Protocol.fromDto(dto).also {
                it.addExtractionQuestion(QuestionId(questionId))
                protocolRepository.create(it.toDto())
            }
        }
    }
}
