package br.all.application.question.find

import br.all.application.question.repository.QuestionDto
import br.all.application.study.repository.StudyReviewDto
import java.util.*

data class QuestionResponseModel(val reviewId: UUID, val questions: List<QuestionDto> ) {
}