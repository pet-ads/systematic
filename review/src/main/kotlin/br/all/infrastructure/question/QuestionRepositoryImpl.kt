package br.all.infrastructure.question

import br.all.application.question.repository.QuestionDto
import br.all.application.question.repository.QuestionRepository
import br.all.domain.model.protocol.ProtocolId
import br.all.domain.model.question.QuestionId
import br.all.infrastructure.shared.toNullable
import org.springframework.stereotype.Repository
import java.util.*

@Repository
open class QuestionRepositoryImpl(private val repository: MongoQuestionRepository): QuestionRepository {
    override fun createOrUpdate(dto: QuestionDto): QuestionDocument = repository.save(dto.toDocument())

    override fun findById(systematicStudyId: UUID, id: UUID) = repository.findById(id).toNullable()?.toDto()

    override fun findAllBySystematicStudyId(systematicStudyId: UUID): List<QuestionDto> =
        repository.findAllBySystematicStudyId(systematicStudyId).map { it.toDto() }
}