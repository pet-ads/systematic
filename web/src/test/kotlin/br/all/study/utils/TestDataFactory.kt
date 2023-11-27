package br.all.study.utils

import br.all.infrastructure.study.StudyReviewDocument
import br.all.infrastructure.study.StudyReviewId
import java.util.*

class TestDataFactory {

    val researcherId: UUID = UUID.randomUUID()
    val systematicStudyId: UUID = UUID.randomUUID()

    fun validPostRequest(researcher: UUID = researcherId, systematicStudyId: UUID = this.systematicStudyId) =
        """
        {
            "researcherId": "$researcher",
            "systematicStudyId": "$systematicStudyId",
            "type": "ARTICLE",
            "title": "Title",
            "year": 2021,
            "authors": "Authors",
            "venue": "Journal",
            "abstract": "Abstract content",
            "keywords": [],
            "source": "Source"
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
        type: String = "ARTICLE",
        title: String = "study",
        year: Int = 2023,
        authors: String = "Lucas",
        venue: String = "JSS",
        abstract: String = "Mussum Ipsum, cacilds vidis litro abertis.",
        keywords: Set<String> = setOf("PET", "IFSP"),
        references: List<String> = emptyList(),
        doi: String? = null,
        sources: Set<String> = setOf("Scopus", "Springer"),
        criteria: Set<String> = setOf("Criteria A", "Criteria B"),
        formAnswers: Map<UUID, String> = mapOf(Pair(UUID.randomUUID(), "Form")),
        robAnswers: Map<UUID, String> = mapOf(Pair(UUID.randomUUID(), "Form")),
        comments: String = "",
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