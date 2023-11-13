package br.all.application.study.find

import br.all.application.study.find.FindAllStudyReviewsService.RequestModel
import br.all.application.study.find.FindAllStudyReviewsService.ResponseModel
import br.all.application.study.repository.StudyReviewRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class FindAllStudyReviewsServiceImpl(
    private val repository: StudyReviewRepository
) : FindAllStudyReviewsService {

    override fun findAllFromReview(request: RequestModel) : ResponseModel {
        val studyReviews = repository.findAllFromReview(request.reviewId)
        return ResponseModel(request.reviewId, studyReviews)
    }
}