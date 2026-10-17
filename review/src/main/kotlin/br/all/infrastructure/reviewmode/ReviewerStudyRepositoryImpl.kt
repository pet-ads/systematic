package br.all.infrastructure.reviewmode

import br.all.application.reviewmode.repository.ReviewerStudyDto
import br.all.application.reviewmode.repository.ReviewerStudyRepository
import br.all.infrastructure.shared.toNullable
import br.all.domain.model.study.StudyReviewStage
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
open class ReviewerStudyRepositoryImpl(
    private val innerRepository: MongoReviewerStudyRepository,
) : ReviewerStudyRepository {

    override fun saveOrUpdate(dto: ReviewerStudyDto) {
        innerRepository.save(dto.toDocument())
    }

    override fun findById(
        systematicStudyId: UUID,
        researcherId: UUID,
        studyId: Long,
        stage: StudyReviewStage,
    ): ReviewerStudyDto? =
        innerRepository.findById(ReviewerStudyDocumentId(systematicStudyId, researcherId, studyId, stage))
            .toNullable()
            ?.toDto()

    override fun findAllByReviewer(
        systematicStudyId: UUID,
        researcherId: UUID,
    ): List<ReviewerStudyDto> =
        innerRepository.findAllByIdSystematicStudyIdAndIdResearcherId(systematicStudyId, researcherId)
            .map { it.toDto() }

    override fun existsById(
        systematicStudyId: UUID,
        researcherId: UUID,
        studyId: Long,
        stage: StudyReviewStage,
    ): Boolean =
        innerRepository.existsById(ReviewerStudyDocumentId(systematicStudyId, researcherId, studyId, stage))
}
