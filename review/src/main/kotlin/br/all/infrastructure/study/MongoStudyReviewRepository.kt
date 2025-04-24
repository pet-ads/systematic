package br.all.infrastructure.study

import br.all.application.study.repository.AnswerDto
import org.springframework.data.mongodb.repository.Aggregation
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Update
import java.util.*


interface MongoStudyReviewRepository : MongoRepository<StudyReviewDocument, StudyReviewId> {

    fun findAllById_SystematicStudyId(reviewID: UUID): List<StudyReviewDocument>

    fun findAllById_SystematicStudyIdAndSearchSourcesContaining(reviewID: UUID, source: String): List<StudyReviewDocument>

    fun findAllById_SystematicStudyIdAndSearchSessionId(reviewID: UUID, searchSessionId: UUID): List<StudyReviewDocument>

    @Update("{ '\$set' : { ?1 : ?2 } }")
    fun findAndUpdateAttributeById(id: StudyReviewId, attributeName:String, newStatus: Any)

    companion object {
        private const val MATCH_QUALITY = """
      { ${'$'}match: { "qualityAnswers.?0": { ${'$'}exists: true } } }
    """
        private const val PROJECT_QUALITY = """
      { ${'$'}project: {
          studyReviewId: ${'$'}_id.studyReviewId,
          answer:        ${'$'}qualityAnswers.?0
      } }
    """
        private const val UNION_WITH = """
      { ${'$'}unionWith: {
          coll: "<yourCollectionName>",
          pipeline: [
              { ${'$'}match: { "formAnswers.?0": { ${'$'}exists: true } } },
              { ${'$'}project: {
                  studyReviewId: ${'$'}_id.studyReviewId,
                  answer:        ${'$'}formAnswers.?0
              } }
          ]
      } }
    """
    }

    @Aggregation(
        pipeline = [ MATCH_QUALITY, PROJECT_QUALITY, UNION_WITH ]
    )
    fun findAllAnswersForQuestion(questionId: String): List<AnswerDto>
}
