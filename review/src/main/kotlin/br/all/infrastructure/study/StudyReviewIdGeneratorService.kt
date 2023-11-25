package br.all.infrastructure.study

import br.all.domain.services.IdGeneratorService
import br.all.infrastructure.shared.SequenceGeneratorService
import org.springframework.stereotype.Service

@Service
class StudyReviewIdGeneratorService (private val sequenceGenerator : SequenceGeneratorService): IdGeneratorService {
    override fun next() = sequenceGenerator.next(StudyReviewDocument.SEQUENCE_NAME)
    override fun reset() = sequenceGenerator.reset(StudyReviewDocument.SEQUENCE_NAME)
}