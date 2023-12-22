package br.all.infrastructure.study

import br.all.application.study.repository.StudyReviewDto
import br.all.application.study.repository.StudyReviewRepository
import br.all.infrastructure.shared.toNullable
import org.springframework.stereotype.Repository
import java.util.*

@Repository
open class StudyReviewRepositoryImpl(private val repository: MongoStudyReviewRepository) : StudyReviewRepository {
    override fun saveOrUpdate(dto: StudyReviewDto): StudyReviewDocument = repository.save(dto.toDocument())

    override fun findById(reviewId: UUID, studyId: Long) =
        repository.findById(StudyReviewId(reviewId, studyId)).toNullable()?.toDto()

    override fun findAllFromReview(reviewId: UUID): List<StudyReviewDto> =
        repository.findAllById_SystematicStudyId(reviewId).map { it.toDto() }

    override fun updateSelectionStatus(reviewId: UUID, studyId: Long, attributeName: String, newStatus: Any) {
        repository.findAndUpdateAttributeById(StudyReviewId(reviewId, studyId), attributeName, newStatus)
    }

    override fun saveOrUpdateBatch(dtos: List<StudyReviewDto>): List<StudyReviewDto> {
        val savedDtos = mutableListOf<StudyReviewDto>()

        for (dto in dtos) {
            val savedDto = repository.save(dto.toDocument()).toDto()
            savedDtos.add(savedDto)
        }

        return savedDtos
    }

}

