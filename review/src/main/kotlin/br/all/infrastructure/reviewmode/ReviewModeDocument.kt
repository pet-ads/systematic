package br.all.infrastructure.reviewmode

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import java.util.UUID

@Document("review_mode")
sealed class ReviewModeDocument(
    @Id open val id: UUID
)

@TypeAlias("SPLIT")
data class SplitDocument(
    @Id override val id: UUID,
) : ReviewModeDocument(id)

@TypeAlias("REPLICATION")
data class ReplicationDocument(
    @Id override val id: UUID,
) : ReviewModeDocument(id)