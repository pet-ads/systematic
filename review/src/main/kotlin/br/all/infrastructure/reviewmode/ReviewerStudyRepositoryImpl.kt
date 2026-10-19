package br.all.infrastructure.reviewmode

import br.all.application.reviewmode.repository.ReviewerStudyDto
import br.all.application.reviewmode.repository.ReviewerStudyRepository
import br.all.infrastructure.shared.toNullable
import br.all.domain.model.study.StudyReviewStage
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
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

    override fun findAllBySystematicStudyIdAndStage(
        systematicStudyId: UUID,
        stage: StudyReviewStage
    ): List<ReviewerStudyDto> =
        innerRepository.findAllBySystematicStudyIdAndStage(systematicStudyId, stage.name).map { it.toDto() }

    override fun findAllByReviewerPaged(
        systematicStudyId: UUID,
        researcherId: UUID,
        stage: StudyReviewStage?,
        pageable: Pageable,
    ): Page<ReviewerStudyDto> {
        val documentPageable = pageable.toDocumentPageable()
        val page = if (stage == null)
            innerRepository.findAllByIdSystematicStudyIdAndIdResearcherId(systematicStudyId, researcherId, documentPageable)
        else
            innerRepository.findAllByIdSystematicStudyIdAndIdResearcherIdAndIdStage(
                systematicStudyId, researcherId, stage, documentPageable
            )
        return page.map { it.toDto() }
    }

    private fun Pageable.toDocumentPageable(): Pageable {
        if (isUnpaged) return this
        val orders = sort.map { order ->
            order.withProperty(if (order.property == "id") "id.studyId" else "studyReview.${order.property}")
        }.toList()
        return PageRequest.of(pageNumber, pageSize, Sort.by(orders))
    }
}
