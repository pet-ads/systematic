package br.all.application.study.repository

import java.util.UUID


interface StudyReviewRepository {
    fun save(studyReviewDto: StudyReviewDto)
    fun findAllFromReview(reviewId: UUID): List<StudyReviewDto>
    fun findById(reviewId: UUID, studyId: Long) : StudyReviewDto?
}
