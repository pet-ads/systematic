package br.all.study.utils

import br.all.application.study.create.CreateStudyReviewService
import br.all.infrastructure.study.StudyReviewDocument
import br.all.infrastructure.study.StudyReviewId
import br.all.infrastructure.study.StudyReviewIdGeneratorService
import java.util.*

class TestDataFactory(private val idService: StudyReviewIdGeneratorService) {

    val researcherId: UUID = UUID.randomUUID()
    val systematicStudyId: UUID = UUID.randomUUID()

    val validPostRequest = CreateStudyReviewService.RequestModel(
        researcherId,
        systematicStudyId,
        "ARTICLE",
        "Title",
        2021,
        "Authors",
        "Journal",
        "abstract",
        emptySet(),
        "source"
    )

    fun reviewDocumentOfId(reviewId: UUID) : StudyReviewDocument {
        val studyId = idService.next()
        val studyReviewId = StudyReviewId(reviewId, studyId)
        return StudyReviewDocument(
            studyReviewId,
            "Article",
            "Test title",
            2023,
            "Lucas",
            "JSS",
            "Mussum Ipsum, cacilds vidis litro abertis. Admodum accumsan disputationi eu sit.",
            setOf("PET", "IFSP"),
            emptyList(),
            "",
            setOf("Scopus", "Springer"),
            setOf("Criteria A", "Criteria B"),
            mapOf(Pair(UUID.randomUUID(), "Form")),
            mapOf(Pair(UUID.randomUUID(), "Quality")),
            "",
            "LOW",
            "UNCLASSIFIED",
            "UNCLASSIFIED"
        )
    }
}