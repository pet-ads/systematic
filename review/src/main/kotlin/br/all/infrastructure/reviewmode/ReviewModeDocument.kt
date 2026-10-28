package br.all.infrastructure.reviewmode

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Persistent
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import java.util.UUID

@Document("review_mode")
data class ReviewModeDocument(
    @Id val id: UUID,
    val selection: ReviewModeStageDocument? = null,
    val extraction: ReviewModeStageDocument? = null
)

@TypeAlias("REVIEW_MODE_STAGE")
sealed class ReviewModeStageDocument

@Persistent
@TypeAlias("SPLIT")
data class SplitDocument(
    val reviewers: List<UUID> = emptyList(),
    val reviewPerStudy: Int,
    val acceptMajority: Boolean = false,
) : ReviewModeStageDocument()

@Persistent
@TypeAlias("REPLICATION")
data class ReplicationDocument(
    val mainReviewer: UUID,
    val secondaryReviewers: Map<UUID, Double> = emptyMap(),
    val noStudySharedBySecondaries: Boolean = true,
    val acceptMajority: Boolean = false,
) : ReviewModeStageDocument()
