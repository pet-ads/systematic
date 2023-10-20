package br.all.infrastructure.study

import br.all.application.study.repository.StudyReviewDto
import br.all.application.study.repository.StudyReviewRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
open class StudyReviewRepositoryImpl (private val repository: MongoStudyReviewRepository): StudyReviewRepository {
    override fun create(studyReviewDto: StudyReviewDto) = repository.save(studyReviewDto.toDocument()).let{}

    override fun findAllFromReview(reviewId: UUID): List<StudyReviewDto> {
        val findAllByReviewId = repository.findAllByReviewId(reviewId)
        return findAllByReviewId.map { it.toDto() }
    }

    override fun findById(reviewId: UUID, studyId: Long) = repository.findByReviewIdAndId(reviewId, studyId).toDto()

}

