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

    @Aggregation(pipeline = [
        """{
        "${'$'}facet": {
            "qualityAnswers": [
                { "${'$'}match": { "qualityAnswers.?0": { "${'$'}exists": true } } },
                { "${'$'}project": {
                    "studyReviewId": "${'$'}_id.studyReviewId",
                    "answersArr": { "${'$'}objectToArray": "${'$'}qualityAnswers" }
                }},
                { "${'$'}unwind": "${'$'}answersArr" },
                { "${'$'}match": { "answersArr.k": "?0" } },
                { "${'$'}project": {
                    "studyReviewId": 1,
                    "answer": "${'$'}answersArr.v"
                }}
            ],
            "formAnswers": [
                { "${'$'}match": { "formAnswers.?0": { "${'$'}exists": true } } },
                { "${'$'}project": {
                    "studyReviewId": "${'$'}_id.studyReviewId",
                    "formAnswersArr": { "${'$'}objectToArray": "${'$'}formAnswers" }
                }},
                { "${'$'}unwind": "${'$'}formAnswersArr" },
                { "${'$'}match": { "formAnswersArr.k": "?0" } },
                { "${'$'}project": {
                    "studyReviewId": 1,
                    "answer": "${'$'}formAnswersArr.v"
                }}
            ]
        }
    }""",
        """{
        "${'$'}project": {
            "allAnswers": { "${'$'}concatArrays": ["${'$'}qualityAnswers", "${'$'}formAnswers"] }
        }
    }""",
        """{ "${'$'}unwind": "${'$'}allAnswers" }""",
        """{
        "${'$'}replaceRoot": { "newRoot": "${'$'}allAnswers" }
    }"""
    ])
    fun findAllAnswersForQuestion(questionId: String): List<AnswerDto>
}
