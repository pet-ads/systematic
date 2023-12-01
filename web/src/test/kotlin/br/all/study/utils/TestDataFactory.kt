package br.all.study.utils

import br.all.domain.model.study.StudyType
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
            "year": ${faker.random.nextInt(1900, 2030)},
            "authors": "${faker.science.scientist()}",
            "venue": "${faker.book.publisher()}",
            "abstract": "${List(faker.random.nextInt(0,30)) { Faker().lorem.words() }.joinToString(" ")}",
            "keywords": ${List(faker.random.nextInt(0,5)){ "\"${faker.lorem.words()}\"" }.joinToString(",", "[", "]")},
            "source": "${faker.lorem.words()}"
        }
        """

    fun invalidPostRequest() =
        """
        {
            "researcherId": "your_researcher_id",
            "systematicStudyId": "your_systematic_study_id",
            "studyType": "ARTICLE",
            "title": "Title",
            "publicationYear": 2021,
            "keywords": [],
            "source": "Source"
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


    fun reviewDocument(
        systematicStudyId: UUID,
        studyReviewId: Long,
        type: String = faker.random.nextEnum(StudyType::class.java).toString(),
        title: String = faker.book.title(),
        year: Int = faker.random.nextInt(1900, 2030),
        authors: String = faker.science.scientist(),
        venue: String = faker.book.publisher(),
        abstract: String = List(faker.random.nextInt(0,20)) { Faker().lorem.words() }.joinToString(" "),
        keywords: Set<String> = Array(faker.random.nextInt(0,5)){ faker.lorem.words()}.toSet(),
        references: List<String> = List(faker.random.nextInt(0,15)){ faker.lorem.words()},
        doi: String? = null,
        sources: Set<String> = Array(faker.random.nextInt(0,5)){ faker.book.publisher()}.toSet(),
        criteria: Map<String,String> = mapOf("Criteria A" to "INCLUSION", "Criteria B" to "EXCLUSION"),
        formAnswers: Map<UUID, String> = mapOf(Pair(UUID.randomUUID(), "Form")),
        robAnswers: Map<UUID, String> = mapOf(Pair(UUID.randomUUID(), "Form")),
        comments: String = List(faker.random.nextInt(0,10)) { Faker().lorem.words() }.joinToString(" "),
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
}