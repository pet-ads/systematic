package br.all.application.question.find

import br.all.application.question.repository.QuestionRepository
import br.all.application.study.find.StudyReviewsResponseModel
import java.util.*

class FindAllQuestionService(private val repository: QuestionRepository) {
    fun findAllQuestionFromReview(reviewId: UUID) : QuestionResponseModel {
        val questions = repository.findAllQuestionFromReview(reviewId)
        return QuestionResponseModel(reviewId, questions)
    }
}