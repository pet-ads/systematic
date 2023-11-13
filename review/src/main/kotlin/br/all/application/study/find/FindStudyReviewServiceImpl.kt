package br.all.application.study.find

import br.all.application.study.find.FindStudyReviewService.*
import br.all.application.study.repository.StudyReviewRepository
import org.springframework.stereotype.Service

@Service
class FindStudyReviewServiceImpl (
    private val repository: StudyReviewRepository
) : FindStudyReviewService {

    override fun findOne(request: RequestModel) : ResponseModel {
        val studyReview = repository.findById(request.reviewId, request.studyReviewId)
        return ResponseModel(studyReview)
    }
}