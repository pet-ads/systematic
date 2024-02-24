package br.all.infrastructure.question

import org.springframework.data.mongodb.repository.MongoRepository
import java.util.UUID

interface MongoQuestionRepository : MongoRepository<QuestionDocument, UUID> {
    fun findAllBySystematicStudyId(systematicStudyId: UUID): List<QuestionDocument>
}