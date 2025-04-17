package br.all.study.utils

import br.all.domain.model.question.QuestionContextEnum
import br.all.domain.model.study.StudyType
import br.all.domain.shared.utils.*
import br.all.infrastructure.question.QuestionDocument
import br.all.infrastructure.study.StudyReviewDocument
import br.all.infrastructure.study.StudyReviewId
import io.github.serpro69.kfaker.Faker
import java.util.*

class TestDataFactory {

    private val researcherId: UUID = UUID.randomUUID()
    val systematicStudyId: UUID = UUID.randomUUID()
    val searchSessionId: UUID = UUID.randomUUID()
    private val faker = Faker()

    fun validPostRequest(researcher: UUID = researcherId, systematicStudyId: UUID = this.systematicStudyId) =
        """
        {
            "researcherId": "$researcher",
            "systematicStudyId": "$systematicStudyId",
            "searchSessionId": "${UUID.randomUUID()}",
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
            "searchSessionId": "${UUID.randomUUID()}",
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
          "studyReviewId": ${listOf(id)},
          "status": "$newStatus",
          "criteria": ["Criteria A", "Criteria B"]
        }
        """.trimIndent()

    fun <T> validAnswerQuestionRequest(studyReviewId: Long, questionId: UUID, type: String, answer: T) =
        """
        {
          "studyReviewId": $studyReviewId,
          "questionId": "$questionId",
          "type": "$type",
          "answer": "$answer"
        }
        """

    fun invalidAnswerRiskOfBiasPatchRequest(studyReviewId: Long, questionId: UUID, type: String) =
        """
        {
          "studyReviewId": $studyReviewId,
          "questionId": "$questionId",
          "type": "$type",
        }
        """

    fun validMarkAsDuplicateRequest(duplicateStudyIds: List<Long>) =
        """
            {
                "duplicatedStudyIds": $duplicateStudyIds
            }
        """

    fun reviewDocument(
        systematicStudyId: UUID,
        studyReviewId: Long,
        searchSessionId: UUID = UUID.randomUUID(),
        type: String = faker.random.nextEnum(StudyType::class.java).toString(),
        title: String = faker.book.title(),
        year: Int = faker.year(),
        authors: String = "Marie Curie",
        venue: String = faker.book.publisher(),
        abstract: String = faker.paragraph(20),
        keywords: Set<String> = faker.wordsList(5).toSet(),
        references: List<String> = faker.paragraphList(4, 5),
        doi: String? = null,
        sources: Set<String> = faker.wordsList(minSize = 1, maxSize = 5).toSet(),
        criteria: Set<String> = setOf("Criteria A"),
        formAnswers: Map<UUID, String> = mapOf(Pair(UUID.randomUUID(), "Form")),
        robAnswers: Map<UUID, String> = mapOf(Pair(UUID.randomUUID(), "Form")),
        comments: String = faker.paragraph(15),
        selectionStatus: String = "UNCLASSIFIED",
        extractionStatus: String = "UNCLASSIFIED",
        readingPriority: String = "LOW",
        score: Int = 0
    ): StudyReviewDocument {
        val studyId = StudyReviewId(systematicStudyId, studyReviewId)
        return StudyReviewDocument(
            studyId, searchSessionId, type, title, year,
            authors, venue, abstract, keywords, references, doi, sources,
            criteria, formAnswers, robAnswers, comments, readingPriority,
            extractionStatus, selectionStatus, score
        )
    }

    fun generateRobQuestionTextualDto(
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
            null,
            QuestionContextEnum.ROB
        )

    fun generateExtractionQuestionTextualDto(
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
            null,
            QuestionContextEnum.EXTRACTION
        )
}