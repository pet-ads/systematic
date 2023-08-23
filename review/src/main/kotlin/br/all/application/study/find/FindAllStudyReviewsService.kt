package br.all.application.study.find

import br.all.application.study.repository.StudyReviewRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class FindAllStudyReviewsService(private val repository: StudyReviewRepository) {
    fun findAllFromReview(reviewId: UUID) : StudyReviewsResponseModel{
        val studyReviews = repository.findAllFromReview(reviewId)
        return StudyReviewsResponseModel(reviewId, studyReviews)
    }
}