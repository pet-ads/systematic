package br.all.infrastructure.reviewmode

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import java.util.UUID

@Document("review_mode")
data class ReviewModeDocument(
    @Id val id: UUID,
    val selection: ReviewModeStageDocument? = null,
    val extraction: ReviewModeStageDocument? = null
)

sealed class ReviewModeStageDocument

@TypeAlias("SPLIT")
class SplitDocument : ReviewModeStageDocument()

@TypeAlias("REPLICATION")
class ReplicationDocument : ReviewModeStageDocument()
