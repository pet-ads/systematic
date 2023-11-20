package br.all.infrastructure.study

import org.springframework.data.mongodb.repository.MongoRepository
import java.util.UUID

interface MongoStudyReviewRepository : MongoRepository<StudyReviewDocument, Long>{
    fun findAllById_ReviewId(reviewID: UUID): List<StudyReviewDocument>
    fun findById(id: StudyReviewId): StudyReviewDocument?

}