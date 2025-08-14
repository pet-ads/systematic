package br.all.infrastructure.study

import br.all.application.study.repository.AnswerDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.Aggregation
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Update
import java.util.*


interface MongoStudyReviewRepository : MongoRepository<StudyReviewDocument, StudyReviewId> {

    fun findAllById_SystematicStudyId(reviewID: UUID, pageable: Pageable): Page<StudyReviewDocument>
    
    fun findAllById_SystematicStudyId(reviewID: UUID): List<StudyReviewDocument>
    
    fun countById_SystematicStudyId(reviewID: UUID): Long

    fun findAllById_SystematicStudyIdAndSearchSourcesContaining(reviewID: UUID, source: String): List<StudyReviewDocument>

    fun findAllById_SystematicStudyIdAndSearchSessionId(reviewID: UUID, searchSessionId: UUID): List<StudyReviewDocument>

    @Update("{ '\$set' : { ?1 : ?2 } }")
    fun findAndUpdateAttributeById(id: StudyReviewId, attributeName:String, newStatus: Any)

    companion object {
        private const val MATCH_QUALITY = """
      {
          ${'$'}match: {
            ${'$'}or: [
              { "qualityAnswers.?0": { ${'$'}exists: true } },
              { "formAnswers.?0": { ${'$'}exists: true } }
            ]
          }
        }
    """
        private const val ANSWERS_QUERY = """
      {
          ${'$'}project: {
            studyReviewId: "${'$'}_id.studyReviewId",
            answer: {
              ${'$'}cond: [
                { ${'$'}ifNull: [ "${'$'}qualityAnswers.?0", null ] },
                "${'$'}qualityAnswers.?0",
                "${'$'}formAnswers.?0"
              ]
            }
          }
        }
    """
    }

    @Aggregation(
        pipeline = [ MATCH_QUALITY, ANSWERS_QUERY]
    )
    fun findAllAnswersForQuestion(questionId: String): List<AnswerDto>
}
