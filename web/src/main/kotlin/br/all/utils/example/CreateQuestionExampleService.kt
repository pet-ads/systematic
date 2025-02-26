package br.all.utils.example

import br.all.application.question.repository.QuestionDto
import br.all.application.question.repository.QuestionRepository
import br.all.domain.model.question.QuestionContextEnum
import br.all.domain.services.UuidGeneratorService
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class CreateQuestionExampleService(
    private val uuidGeneratorService: UuidGeneratorService,
    private val questionRepository: QuestionRepository
) {
    fun createQuestion(
        reviewId: UUID,
        code: String,
        title: String,
        context: QuestionContextEnum,
        type: String = "TEXTUAL"
    ): QuestionDto {
        val newQuestion = QuestionDto(
            questionId = uuidGeneratorService.next(),
            systematicStudyId = reviewId,
            code = code,
            description = title,
            questionType = type,
            context = context
        )
        questionRepository.createOrUpdate(newQuestion)
        return newQuestion
    }
}