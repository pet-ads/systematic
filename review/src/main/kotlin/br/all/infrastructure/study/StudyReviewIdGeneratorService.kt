package br.all.infrastructure.study

import br.all.domain.services.IdGeneratorService
import br.all.infrastructure.shared.SequenceGeneratorService
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class StudyReviewIdGeneratorService (private val sequenceGenerator : SequenceGeneratorService): IdGeneratorService {
    override fun next(reviewId: UUID) = sequenceGenerator.next("${StudyReviewDocument.SEQUENCE_NAME}_$reviewId")
    override fun reset(reviewId: UUID) = sequenceGenerator.reset("${StudyReviewDocument.SEQUENCE_NAME}_$reviewId")
}