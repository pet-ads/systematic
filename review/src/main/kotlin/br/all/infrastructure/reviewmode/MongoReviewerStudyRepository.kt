package br.all.infrastructure.reviewmode

import br.all.domain.model.study.StudyReviewStage
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.UUID

interface MongoReviewerStudyRepository : MongoRepository<ReviewerStudyDocument, ReviewerStudyDocumentId> {
    fun findAllByIdSystematicStudyIdAndIdResearcherId(
        systematicStudyId: UUID,
        researcherId: UUID,
    ): List<ReviewerStudyDocument>

    fun findAllByIdSystematicStudyIdAndIdStage(
        systematicStudyId: UUID,
        stage: String,
    ): List<ReviewerStudyDocument>

    fun findAllByIdSystematicStudyIdAndIdResearcherId(
        systematicStudyId: UUID,
        researcherId: UUID,
        pageable: Pageable,
    ): Page<ReviewerStudyDocument>

    fun findAllByIdSystematicStudyIdAndIdResearcherIdAndIdStage(
        systematicStudyId: UUID,
        researcherId: UUID,
        stage: StudyReviewStage,
        pageable: Pageable,
    ): Page<ReviewerStudyDocument>
}
