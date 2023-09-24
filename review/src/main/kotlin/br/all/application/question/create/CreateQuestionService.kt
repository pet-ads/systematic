package br.all.domain.services

import br.all.application.question.create.QuestionRequestModel
import br.all.application.question.repository.QuestionDto
import br.all.application.question.repository.QuestionRepository
import br.all.domain.model.question.LabeledScale
import br.all.domain.model.question.Question
import java.util.*

class CreateQuestionService(
    private val repository: QuestionRepository,
    private val idGenerator: IdGeneratorService
    ) {
    fun createQuestion(reviewId: UUID, question: QuestionRequestModel): QuestionDto {
        val studyId = idGenerator.next()
        val question =
            repository.create(question.toDto())
        return repository.findById(reviewId, questionId)
    }
}