package br.all.application.repositoryStub

import br.all.application.study.repository.StudyReviewDto
import br.all.application.study.repository.StudyReviewRepository
import java.util.*

class StudyReviewRepositoryFake : StudyReviewRepository{

    val mapStudyReview = mutableMapOf<Pair<UUID, Long>, StudyReviewDto>()
    override fun create(studyReviewDto: StudyReviewDto) {
        mapStudyReview.put(studyReviewDto.reviewId to studyReviewDto.id, studyReviewDto)
    }

    override fun findAllFromReview(reviewId: UUID): List<StudyReviewDto> {
        return mapStudyReview.values.toList()
    }

    override fun findById(reviewId: UUID, studyId: Long): StudyReviewDto? {
        return mapStudyReview.get(reviewId to studyId)
    }


}