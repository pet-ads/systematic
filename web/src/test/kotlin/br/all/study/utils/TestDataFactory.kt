package br.all.study.utils

import br.all.domain.model.study.StudyType
import br.all.domain.shared.utils.*
import br.all.infrastructure.question.QuestionDocument
import br.all.infrastructure.study.StudyReviewDocument
import br.all.infrastructure.study.StudyReviewId
import io.github.serpro69.kfaker.Faker
import java.util.*

class TestDataFactory {

    val researcherId: UUID = UUID.randomUUID()
    val systematicStudyId: UUID = UUID.randomUUID()
    private val faker = Faker()

    fun validPostRequest(researcher: UUID = researcherId, systematicStudyId: UUID = this.systematicStudyId) =
        """
        {
            "researcherId": "$researcher",
            "systematicStudyId": "$systematicStudyId",
            "type": "${faker.random.nextEnum(StudyType::class.java)}",
            "title": "${faker.book.title()}",
            "year": ${faker.year()},
            "authors": "${faker.science.scientist()}",
            "venue": "${faker.book.publisher()}",
            "abstract": "${faker.paragraph(30)}",
            "keywords": ${faker.jsonWordsArray(5)},
            "source": "${faker.lorem.words()}"
        }
        """

    fun invalidPostRequest() =
        """
        {
            "researcherId": "your_researcher_id",
            "systematicStudyId": "your_systematic_study_id",
            "studyType": "${faker.random.nextEnum(StudyType::class.java)}E",
            "title": "${faker.book.title()}",
            "publicationYear": ${faker.year()},
            "keywords": ${faker.jsonWordsArray(0)},
            "source": "${faker.lorem.words()}"
        }
        """

    fun validPutRequest(systematicStudyId: UUID = this.systematicStudyId, studyReviewId: Long) =
        """
        {
            "studyReviewId": "$studyReviewId",
            "systematicStudyId": "$systematicStudyId",
            "type": "${faker.random.nextEnum(StudyType::class.java)}",
            "title": "${faker.book.title()}",
            "year": ${faker.year()},
            "authors": "${faker.science.scientist()}",
            "venue": "${faker.book.publisher()}",
            "abstract": "${faker.paragraph(30)}",
            "keywords": ${faker.jsonWordsArray(5)},
            "source": "${faker.lorem.words()}"
        }
        """

    fun invalidPutRequest() =
        """
        {
            
            "year": ${faker.year()},
            "authors": "${faker.science.scientist()}",
            "venue": "${faker.book.publisher()}",
            "abstract": "${faker.paragraph(30)}",
            "keywords": ${faker.jsonWordsArray(5)},
            "source": "${faker.lorem.words()}"
        }
        """

    fun validStatusUpdatePatchRequest(id: Long, newStatus: String) =
        """
        {
          "researcherId": "$researcherId",
          "systematicStudyId": "$systematicStudyId",
          "studyReviewId": $id,
          "status": "$newStatus"
        }
        """.trimIndent()


    fun <T> validAnswerRiskOfBiasPatchRequest(studyReviewId: Long, questionId: UUID, type: String, answer: T) =
        """
        {
          "researcherId": "$researcherId",
          "systematicStudyId": "$systematicStudyId",
          "studyReviewId": $studyReviewId,
          "status": "$questionId",
          "type": "$type",
          "answer": "$answer"
        }
        """.trimIndent()


    fun reviewDocument(
        systematicStudyId: UUID,
        studyReviewId: Long,
        type: String = faker.random.nextEnum(StudyType::class.java).toString(),
        title: String = faker.book.title(),
        year: Int = faker.year(),
        authors: String = faker.science.scientist(),
        venue: String = faker.book.publisher(),
        abstract: String = faker.paragraph(20),
        keywords: Set<String> = faker.wordsList(5).toSet(),
        references: List<String> = faker.paragraphList(4, 5),
        doi: String? = null,
        sources: Set<String> = faker.wordsList(minSize = 1, maxSize = 5).toSet(),
        criteria: Map<String,String> = mapOf("Criteria A" to "INCLUSION", "Criteria B" to "EXCLUSION"),
        formAnswers: Map<UUID, String> = mapOf(Pair(UUID.randomUUID(), "Form")),
        robAnswers: Map<UUID, String> = mapOf(Pair(UUID.randomUUID(), "Form")),
        comments: String = faker.paragraph(15),
        selectionStatus: String = "UNCLASSIFIED",
        extractionStatus: String = "UNCLASSIFIED",
        readingPriority: String = "LOW"
    ): StudyReviewDocument {
        val studyId = StudyReviewId(systematicStudyId, studyReviewId)
        return StudyReviewDocument(
            studyId, type, title, year,
            authors, venue, abstract, keywords, references, doi, sources,
            criteria, formAnswers, robAnswers, comments, readingPriority,
            extractionStatus, selectionStatus
        )
    }

    fun generateQuestionTextualDto(
        questionId: UUID,
        systematicStudyId: UUID = this.systematicStudyId,
        code: String = faker.lorem.words(),
        description: String = faker.lorem.words(),
    ) =
        QuestionDocument(
            questionId,
            systematicStudyId,
            code,
            description,
            "TEXTUAL",
            null,
            null,
            null,
            null
        )

}