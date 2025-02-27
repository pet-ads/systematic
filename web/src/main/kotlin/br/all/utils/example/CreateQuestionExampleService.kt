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
        type: String,
        scales: Map<String, Int>? = null,
        higher: Int? = null,
        lower: Int? = null,
        options: List<String>? = null
    ): QuestionDto {
        val newQuestion = QuestionDto(
            questionId = uuidGeneratorService.next(),
            systematicStudyId = reviewId,
            code = code,
            description = title,
            questionType = type,
            context = context,
            scales = scales,
            higher = higher,
            lower = lower,
            options = options
        )
        questionRepository.createOrUpdate(newQuestion)
        return newQuestion
    }
}