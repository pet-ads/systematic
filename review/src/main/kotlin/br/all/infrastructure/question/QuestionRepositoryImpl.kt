package br.all.infrastructure.question

import br.all.application.question.repository.QuestionDto
import br.all.application.question.repository.QuestionRepository
import br.all.domain.model.question.QuestionContextEnum
import br.all.infrastructure.shared.toNullable
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class QuestionRepositoryImpl(private val repository: MongoQuestionRepository) : QuestionRepository {

    override fun createOrUpdate(dto: QuestionDto) {
        repository.save(dto.toDocument())
    }

    override fun findById(systematicStudyId: UUID, id: UUID): QuestionDto? {
        return repository.findById(id).toNullable()?.toDto()
    }

    override fun findAllBySystematicStudyId(systematicStudyId: UUID, context: QuestionContextEnum?): List<QuestionDto> {
        val allQuestions = repository.findAllBySystematicStudyId(systematicStudyId)

        val filteredQuestions = if (context != null) {
            allQuestions.filter { it.context == context }
        } else {
            allQuestions
        }

        return filteredQuestions.map { it.toDto() }
    }

    override fun deleteById(systematicStudyId: UUID, id: UUID) {
        repository.deleteById(id)
    }
}