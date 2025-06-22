package br.all.application.study.repository

import java.util.UUID


interface StudyReviewRepository {
    fun saveOrUpdate(dto: StudyReviewDto): Any
    fun findAllFromReview(reviewId: UUID): List<StudyReviewDto>
    fun findAllBySource(reviewId: UUID, source: String): List<StudyReviewDto>
    fun findAllBySession(reviewId: UUID, searchSessionId: UUID): List<StudyReviewDto>
    fun findById(reviewId: UUID, studyId: Long) : StudyReviewDto?
    fun updateSelectionStatus(reviewId: UUID, studyId: Long, attributeName: String, newStatus: Any)
    fun saveOrUpdateBatch(dtos: List<StudyReviewDto>)
    fun findAllQuestionAnswers(reviewId: UUID, questionId: UUID): List<AnswerDto>
}

data class AnswerDto(
    val studyReviewId: Long,
    val answer: String
)