package br.all.infrastructure.study

import br.all.application.study.repository.StudyReviewDto
import br.all.application.study.repository.StudyReviewRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
open class StudyReviewRepositoryImpl(private val repository: MongoStudyReviewRepository) : StudyReviewRepository {
    override fun save(studyReviewDto: StudyReviewDto) = repository.save(studyReviewDto.toDocument()).let {}

    override fun findById(reviewId: UUID, studyId: Long) =
        repository.findById(StudyReviewId(reviewId, studyId))?.toDto()

    override fun findAllFromReview(reviewId: UUID): List<StudyReviewDto> {
        val findAllByReviewId = repository.findAllById_ReviewId(reviewId)
        return findAllByReviewId.map { it.toDto() }
    }

}

