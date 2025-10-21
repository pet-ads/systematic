package br.all.application.study.repository

import br.all.domain.model.study.SelectionStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID


interface StudyReviewRepository {
    fun saveOrUpdate(dto: StudyReviewDto): Any
    fun findAllFromReview(reviewId: UUID, pageable: Pageable = Pageable.unpaged()): List<StudyReviewDto>
    fun findAllFromReviewPaged(reviewId: UUID, pageable: Pageable = Pageable.unpaged()): Page<StudyReviewDto>
    fun findAllBySource(reviewId: UUID, source: String): List<StudyReviewDto>
    fun findAllBySession(reviewId: UUID, searchSessionId: UUID): List<StudyReviewDto>
    fun findAllBySessionPaged(
        reviewId: UUID,
        searchSessionId: UUID,
        pageable: Pageable = Pageable.unpaged()
    ): Page<StudyReviewDto>

    fun findAllBySessionPagedAndSelectionStatus(
        reviewId: UUID,
        searchSessionId: UUID,
        status: SelectionStatus,
        pageable: Pageable = Pageable.unpaged()
    ): Page<StudyReviewDto>

    fun findById(reviewId: UUID, studyId: Long): StudyReviewDto?
    fun updateSelectionStatus(reviewId: UUID, studyId: Long, attributeName: String, newStatus: Any)
    fun saveOrUpdateBatch(dtos: List<StudyReviewDto>)
    fun findAllQuestionAnswers(reviewId: UUID, questionId: UUID): List<AnswerDto>
}

data class AnswerDto(
    val studyReviewId: Long,
    val answer: String
)