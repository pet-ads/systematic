package br.all.application.repositoryFake

import br.all.application.study.repository.StudyReviewDto
import br.all.application.study.repository.StudyReviewRepository
import java.util.*

class StudyReviewRepositoryFake : StudyReviewRepository {

    val mapStudyReview = mutableMapOf<Pair<UUID, Long>, StudyReviewDto>()
    override fun saveOrUpdate(dto: StudyReviewDto) {
        mapStudyReview.put(dto.reviewId to dto.studyId, dto)
    }

    override fun findAllFromReview(reviewId: UUID): List<StudyReviewDto> {
        return mapStudyReview.values.toList()
    }

    override fun findById(reviewId: UUID, studyId: Long): StudyReviewDto? {
        return mapStudyReview.get(reviewId to studyId)
    }

    override fun updateSelectionStatus(reviewId: UUID, studyId: Long, attributeName: String, newStatus: Any) {
        TODO("Not yet implemented")
    }
}