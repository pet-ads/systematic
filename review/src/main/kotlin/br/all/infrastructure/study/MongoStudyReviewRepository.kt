package br.all.infrastructure.study

import org.springframework.data.mongodb.repository.MongoRepository
import java.util.UUID

interface MongoStudyReviewRepository : MongoRepository<StudyReviewDocument, Long>{
    fun findAllByReviewId(reviewID: UUID): List<StudyReviewDocument>
    fun findByReviewIdAndId(reviewID: UUID, studyId: Long): StudyReviewDocument

}