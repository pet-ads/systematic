package br.all.infrastructure.reviewmode

import org.springframework.data.mongodb.repository.MongoRepository
import java.util.UUID

interface MongoReviewerStudyRepository : MongoRepository<ReviewerStudyDocument, ReviewerStudyDocumentId> {
    fun findAllByIdSystematicStudyIdAndIdResearcherId(
        systematicStudyId: UUID,
        researcherId: UUID,
    ): List<ReviewerStudyDocument>

    fun findAllBySystematicStudyIdAndStage(
        systematicStudyId: UUID,
        stage: String,
    ): List<ReviewerStudyDocument>
}
