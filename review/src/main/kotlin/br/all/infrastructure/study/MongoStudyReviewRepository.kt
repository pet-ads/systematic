package br.all.infrastructure.study

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Update
import java.util.*


interface MongoStudyReviewRepository : MongoRepository<StudyReviewDocument, StudyReviewId> {

    fun findAllById_SystematicStudyId(reviewID: UUID): List<StudyReviewDocument>

    fun findAllById_SystematicStudyIdAndSearchSourcesContaining(reviewID: UUID, source: String): List<StudyReviewDocument>

    fun findAllById_SystematicStudyIdAndSearchSessionId(reviewID: UUID, searchSessionId: UUID): List<StudyReviewDocument>

    @Update("{ '\$set' : { ?1 : ?2 } }")
    fun findAndUpdateAttributeById(id: StudyReviewId, attributeName:String, newStatus: Any)

}
