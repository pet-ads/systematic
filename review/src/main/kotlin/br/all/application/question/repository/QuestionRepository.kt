package br.all.application.question.repository

import br.all.application.study.repository.StudyReviewDto
import java.util.*

interface QuestionRepository {
    fun create(QuestionDto: QuestionDto)
    fun findAllQuestionFromReview(reviewId: UUID): List<QuestionDto>
    fun findById(reviewId: UUID, questionId: Long) : QuestionDto
}