package br.all.infrastructure.reviewmode

import br.all.infrastructure.study.StudyReviewDocument
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.io.Serializable
import java.util.UUID

@Document("reviewer_study")
data class ReviewerStudyDocument(
    @Id val id: ReviewerStudyDocumentId,
    val studyReview: StudyReviewDocument,
)

data class ReviewerStudyDocumentId(
    val systematicStudyId: UUID,
    val researcherId: UUID,
    val studyId: Long,
) : Serializable
